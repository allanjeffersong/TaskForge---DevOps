import React, { useState, useEffect, useCallback } from 'react';
import { taskService } from './taskService';
import './App.css';

const PRIORITIES = ['BAIXA', 'MEDIA', 'ALTA'];
const STATUSES = ['PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA'];

const PRIORITY_LABEL = { BAIXA: 'Baixa', MEDIA: 'Média', ALTA: 'Alta' };
const STATUS_LABEL = { PENDENTE: 'Pendente', EM_ANDAMENTO: 'Em Andamento', CONCLUIDA: 'Concluída' };

const emptyForm = { title: '', description: '', priority: 'MEDIA', status: 'PENDENTE', dueDate: '' };

function App() {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [formError, setFormError] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterPriority, setFilterPriority] = useState('');
  const [confirmDelete, setConfirmDelete] = useState(null);

  const loadTasks = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await taskService.getAll({ status: filterStatus, priority: filterPriority });
      setTasks(data);
    } catch {
      setError('Não foi possível carregar as tarefas. Verifique se o servidor está rodando.');
    } finally {
      setLoading(false);
    }
  }, [filterStatus, filterPriority]);

  useEffect(() => { loadTasks(); }, [loadTasks]);

  const showSuccess = (msg) => {
    setSuccess(msg);
    setTimeout(() => setSuccess(''), 3000);
  };

  const openCreate = () => {
    setEditingTask(null);
    setForm(emptyForm);
    setFormError('');
    setShowModal(true);
  };

  const openEdit = (task) => {
    setEditingTask(task);
    setForm({
      title: task.title,
      description: task.description || '',
      priority: task.priority,
      status: task.status,
      dueDate: task.dueDate || '',
    });
    setFormError('');
    setShowModal(true);
  };

  const closeModal = () => { setShowModal(false); setEditingTask(null); };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.title.trim()) { setFormError('Título é obrigatório.'); return; }
    setFormError('');
    try {
      if (editingTask) {
        await taskService.update(editingTask.id, form);
        showSuccess('Tarefa atualizada com sucesso!');
      } else {
        await taskService.create(form);
        showSuccess('Tarefa criada com sucesso!');
      }
      closeModal();
      loadTasks();
    } catch (err) {
      setFormError(err.message || 'Erro ao salvar tarefa.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await taskService.delete(id);
      setConfirmDelete(null);
      showSuccess('Tarefa removida com sucesso!');
      loadTasks();
    } catch {
      setError('Erro ao remover tarefa.');
    }
  };

  const counts = {
    total: tasks.length,
    pendente: tasks.filter(t => t.status === 'PENDENTE').length,
    emAndamento: tasks.filter(t => t.status === 'EM_ANDAMENTO').length,
    concluida: tasks.filter(t => t.status === 'CONCLUIDA').length,
  };

  return (
      <div className="app">
        <header className="header">
          <div className="header-inner">
            <div className="logo">
              <span className="logo-icon">⚡</span>
              <span className="logo-text">TaskForge</span>
            </div>
            <button className="btn-new" onClick={openCreate}>+ Nova Tarefa</button>
          </div>
        </header>

        <main className="main">
          {error && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}

          <div className="stats-row">
            <div className="stat-card"><span className="stat-num">{counts.total}</span><span className="stat-label">Total</span></div>
            <div className="stat-card stat-pendente"><span className="stat-num">{counts.pendente}</span><span className="stat-label">Pendentes</span></div>
            <div className="stat-card stat-andamento"><span className="stat-num">{counts.emAndamento}</span><span className="stat-label">Em Andamento</span></div>
            <div className="stat-card stat-concluida"><span className="stat-num">{counts.concluida}</span><span className="stat-label">Concluídas</span></div>
          </div>

          <div className="filters">
            <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
              <option value="">Todos os status</option>
              {STATUSES.map(s => <option key={s} value={s}>{STATUS_LABEL[s]}</option>)}
            </select>
            <select value={filterPriority} onChange={e => setFilterPriority(e.target.value)}>
              <option value="">Todas as prioridades</option>
              {PRIORITIES.map(p => <option key={p} value={p}>{PRIORITY_LABEL[p]}</option>)}
            </select>
            <button className="btn-clear" onClick={() => { setFilterStatus(''); setFilterPriority(''); }}>Limpar</button>
          </div>

          {loading ? (
              <div className="loading"><div className="spinner"></div><p>Carregando tarefas...</p></div>
          ) : tasks.length === 0 ? (
              <div className="empty">
                <span className="empty-icon">📋</span>
                <p>Nenhuma tarefa encontrada.</p>
                <button className="btn-new" onClick={openCreate}>Criar primeira tarefa</button>
              </div>
          ) : (
              <div className="task-list">
                {tasks.map(task => (
                    <div key={task.id} className={`task-card priority-${task.priority.toLowerCase()}`}>
                      <div className="task-header">
                        <h3 className="task-title">{task.title}</h3>
                        <div className="task-badges">
                          <span className={`badge badge-priority-${task.priority.toLowerCase()}`}>{PRIORITY_LABEL[task.priority]}</span>
                          <span className={`badge badge-status-${task.status.toLowerCase().replace('_', '-')}`}>{STATUS_LABEL[task.status]}</span>
                        </div>
                      </div>
                      {task.description && <p className="task-desc">{task.description}</p>}
                      {task.dueDate && <p className="task-due">📅 Prazo: {new Date(task.dueDate + 'T00:00:00').toLocaleDateString('pt-BR')}</p>}
                      <div className="task-actions">
                        <button className="btn-edit" onClick={() => openEdit(task)}>Editar</button>
                        <button className="btn-delete" onClick={() => setConfirmDelete(task)}>Excluir</button>
                      </div>
                    </div>
                ))}
              </div>
          )}
        </main>

        {showModal && (
            <div className="modal-overlay" onClick={closeModal}>
              <div className="modal" onClick={e => e.stopPropagation()}>
                <div className="modal-header">
                  <h2>{editingTask ? 'Editar Tarefa' : 'Nova Tarefa'}</h2>
                  <button className="modal-close" onClick={closeModal}>×</button>
                </div>
                <form onSubmit={handleSubmit} className="modal-form">
                  {formError && <div className="alert alert-error">{formError}</div>}
                  <div className="field">
                    <label>Título *</label>
                    <input
                        type="text"
                        value={form.title}
                        onChange={e => setForm({ ...form, title: e.target.value })}
                        placeholder="Título da tarefa"
                        maxLength={100}
                    />
                  </div>
                  <div className="field">
                    <label>Descrição</label>
                    <textarea
                        value={form.description}
                        onChange={e => setForm({ ...form, description: e.target.value })}
                        placeholder="Descrição da tarefa (opcional)"
                        rows={3}
                    />
                  </div>
                  <div className="field-row">
                    <div className="field">
                      <label>Prioridade *</label>
                      <select value={form.priority} onChange={e => setForm({ ...form, priority: e.target.value })}>
                        {PRIORITIES.map(p => <option key={p} value={p}>{PRIORITY_LABEL[p]}</option>)}
                      </select>
                    </div>
                    <div className="field">
                      <label>Status</label>
                      <select value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}>
                        {STATUSES.map(s => <option key={s} value={s}>{STATUS_LABEL[s]}</option>)}
                      </select>
                    </div>
                  </div>
                  <div className="field">
                    <label>Data de Prazo</label>
                    <input
                        type="date"
                        value={form.dueDate}
                        onChange={e => setForm({ ...form, dueDate: e.target.value })}
                    />
                  </div>
                  <div className="modal-actions">
                    <button type="button" className="btn-cancel" onClick={closeModal}>Cancelar</button>
                    <button type="submit" className="btn-save">{editingTask ? 'Salvar' : 'Criar'}</button>
                  </div>
                </form>
              </div>
            </div>
        )}

        {confirmDelete && (
            <div className="modal-overlay" onClick={() => setConfirmDelete(null)}>
              <div className="modal modal-confirm" onClick={e => e.stopPropagation()}>
                <h2>Confirmar Exclusão</h2>
                <p>Deseja excluir a tarefa <strong>"{confirmDelete.title}"</strong>?</p>
                <div className="modal-actions">
                  <button className="btn-cancel" onClick={() => setConfirmDelete(null)}>Cancelar</button>
                  <button className="btn-delete-confirm" onClick={() => handleDelete(confirmDelete.id)}>Excluir</button>
                </div>
              </div>
            </div>
        )}
      </div>
  );
}

export default App;