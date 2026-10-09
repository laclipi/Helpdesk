"use client";

import { useEffect, useState } from "react";
import LoginForm from "@/components/login-form";
import {
  createTicket,
  getMyTickets,
  type Ticket,
  type TicketPriority,
} from "@/lib/api";

import NewTicketForm from "@/components/new-ticket-form";

// Devuelve los colores adecuados para cada estado.
function getStatusStyle(status: string) {
  switch (status) {
    case "Abierto":
      return "bg-blue-50 text-blue-700";
    case "En progreso":
      return "bg-amber-50 text-amber-700";
    case "Resuelto":
      return "bg-emerald-50 text-emerald-700";
    case "Cerrado":
      return "bg-slate-100 text-slate-600";
    default:
      return "bg-slate-100 text-slate-600";
  }
}

// Devuelve el color de cada prioridad.
function getPriorityStyle(priority: string) {
  switch (priority) {
    case "Alta":
      return "text-rose-600";
    case "Media":
      return "text-amber-600";
    default:
      return "text-slate-500";
  }
}

// Traduce los estados del backend al español.
function getStatusLabel(status: Ticket["status"]) {
  const labels: Record<Ticket["status"], string> = {
    OPEN: "Abierto",
    IN_PROGRESS: "En progreso",
    RESOLVED: "Resuelto",
    CLOSED: "Cerrado",
  };

  return labels[status];
}

// Traduce las prioridades del backend al español.
function getPriorityLabel(priority: Ticket["priority"]) {
  const labels: Record<Ticket["priority"], string> = {
    LOW: "Baja",
    MEDIUM: "Media",
    HIGH: "Alta",
  };

  return labels[priority];
}

// Formatea el identificador numérico del ticket.
function formatTicketId(id: number) {
  return `#TK-${String(id).padStart(4, "0")}`;
}

export default function Home() {
  // Guarda el token y el correo de la sesión iniciada.
  const [session, setSession] = useState<{
    token: string;
    email: string;
  } | null>(null);

  // Guarda los tickets reales recibidos del backend.
  const [tickets, setTickets] = useState<Ticket[]>([]);

  // Controla el estado de carga.
  const [isLoadingTickets, setIsLoadingTickets] = useState(false);

  // Guarda los posibles errores de la petición.
  const [ticketsError, setTicketsError] = useState<string | null>(null);

  // Controla si el formulario para crear tickets está visible.
  const [isNewTicketFormOpen, setIsNewTicketFormOpen] = useState(false);

  // Carga los tickets cuando hay una sesión autenticada.
  useEffect(() => {
    if (!session) return;
    // Guardamos el token para usarlo de forma segura dentro de la función asíncrona.
    const token = session.token;

    let cancelled = false;

    async function loadTickets() {
      setIsLoadingTickets(true);
      setTicketsError(null);

      try {
        // Envía el JWT al endpoint GET /tickets.
        const data = await getMyTickets(token);

        if (!cancelled) {
          setTickets(data);
        }
      } catch (error: unknown) {
        if (!cancelled) {
          setTicketsError(
            error instanceof Error
              ? error.message
              : "No se pudieron cargar los tickets.",
          );
        }
      } finally {
        if (!cancelled) {
          setIsLoadingTickets(false);
        }
      }
    }

    loadTickets();

    // Evita actualizar el estado después de limpiar el efecto.
    return () => {
      cancelled = true;
    };
  }, [session]);

  // Calcula las métricas a partir de los tickets reales.
  const openTickets = tickets.filter(
    (ticket) => ticket.status === "OPEN",
  ).length;

  const inProgressTickets = tickets.filter(
    (ticket) => ticket.status === "IN_PROGRESS",
  ).length;

  const resolvedTickets = tickets.filter(
    (ticket) => ticket.status === "RESOLVED" || ticket.status === "CLOSED",
  ).length;

  // Sin sesión, mostramos el formulario de acceso.
  if (!session) {
    return (
      <LoginForm onLogin={(token, email) => setSession({ token, email })} />
    );
  }

  return (
    <div className="min-h-screen">
      {/* Navegación lateral */}
      <aside className="fixed inset-y-0 left-0 hidden w-64 border-r border-slate-200 bg-white p-6 md:flex md:flex-col">
        <a href="#" className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-700 text-lg font-bold text-white">
            H
          </div>
          <div>
            <p className="text-lg font-bold tracking-tight text-slate-900">
              Helpdesk
            </p>
            <p className="text-xs text-slate-500">Ticket management</p>
          </div>
        </a>

        <p className="mb-3 mt-10 text-xs font-semibold uppercase tracking-wider text-slate-400">
          Workspace
        </p>

        <nav className="space-y-1">
          <a
            href="#dashboard"
            className="flex items-center gap-3 rounded-lg bg-blue-50 px-3 py-2.5 text-sm font-semibold text-blue-700"
          >
            <span>▦</span>
            Dashboard
          </a>
          <a
            href="#tickets"
            className="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm text-slate-600 transition hover:bg-slate-50 hover:text-slate-900"
          >
            <span>▤</span>
            Mis tickets
            <span className="ml-auto rounded-md bg-slate-100 px-2 py-0.5 text-xs">
              {tickets.length}
            </span>
          </a>
        </nav>

        <div className="mt-auto rounded-xl border border-slate-200 bg-slate-50 p-4">
          <p className="text-sm font-semibold text-slate-800">
            ¿Necesitas ayuda?
          </p>
          <p className="mt-1 text-xs leading-5 text-slate-500">
            Consulta tus incidencias y sigue su estado desde aquí.
          </p>
        </div>
      </aside>

      {/* Contenido principal */}
      <main className="md:ml-64">
        <header className="flex h-[72px] items-center justify-between border-b border-slate-200 bg-white px-5 sm:px-8">
          <div>
            <p className="text-sm text-slate-500">Workspace / Overview</p>
          </div>

          <div className="flex items-center gap-3">
            <button
              type="button"
              aria-label="Notificaciones"
              className="rounded-lg border border-slate-200 px-3 py-2 text-sm text-slate-600 transition hover:bg-slate-50"
            >
              ♧
            </button>
            <div className="flex h-9 w-9 items-center justify-center rounded-full bg-blue-100 text-sm font-semibold text-blue-700">
              {session.email.slice(0, 2).toUpperCase()}
            </div>
          </div>
        </header>

        <div id="dashboard" className="mx-auto max-w-7xl px-5 py-8 sm:px-8">
          {/* Encabezado del dashboard */}
          <section className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
            <div>
              <p className="text-sm font-medium text-blue-700">
                SUPPORT CENTER
              </p>
              <h1 className="mt-2 text-3xl font-bold tracking-tight text-slate-900">
                Dashboard
              </h1>
              <p className="mt-2 text-sm text-slate-500">
                Consulta y gestiona tus solicitudes de soporte.
              </p>
            </div>

            <button
              type="button"
              onClick={() => setIsNewTicketFormOpen(true)}
              className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-700 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-800"
            >
              <span className="text-lg leading-none">+</span>
              Nuevo ticket
            </button>
          </section>

          {/* Métricas calculadas a partir de la API */}
          <section className="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
              <p className="text-sm text-slate-500">Total de tickets</p>
              <div className="mt-3 flex items-end justify-between">
                <p className="text-3xl font-bold text-slate-900">
                  {tickets.length}
                </p>
                <span className="text-xs text-slate-400">Todos</span>
              </div>
            </article>

            <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
              <p className="text-sm text-slate-500">Abiertos</p>
              <div className="mt-3 flex items-end justify-between">
                <p className="text-3xl font-bold text-slate-900">
                  {openTickets}
                </p>
                <span className="rounded-full bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-700">
                  Pendientes
                </span>
              </div>
            </article>

            <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
              <p className="text-sm text-slate-500">En progreso</p>
              <div className="mt-3 flex items-end justify-between">
                <p className="text-3xl font-bold text-slate-900">
                  {inProgressTickets}
                </p>
                <span className="rounded-full bg-amber-50 px-2.5 py-1 text-xs font-medium text-amber-700">
                  En curso
                </span>
              </div>
            </article>

            <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
              <p className="text-sm text-slate-500">Resueltos</p>
              <div className="mt-3 flex items-end justify-between">
                <p className="text-3xl font-bold text-slate-900">
                  {resolvedTickets}
                </p>
                <span className="rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-medium text-emerald-700">
                  Resueltos o cerrados
                </span>
              </div>
            </article>
          </section>

          {/* Tabla de tickets */}
          <section
            id="tickets"
            className="mt-8 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm"
          >
            <div className="flex flex-col gap-3 border-b border-slate-200 p-5 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <h2 className="font-semibold text-slate-900">
                  Tickets recientes
                </h2>
                <p className="mt-1 text-sm text-slate-500">
                  Resumen de las últimas solicitudes.
                </p>
              </div>

              <button
                type="button"
                className="self-start rounded-lg border border-slate-200 px-3 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-50 sm:self-auto"
              >
                Ver todos
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full min-w-[720px] text-left text-sm">
                <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
                  <tr>
                    <th className="px-5 py-3 font-medium">Ticket</th>
                    <th className="px-5 py-3 font-medium">Prioridad</th>
                    <th className="px-5 py-3 font-medium">Estado</th>
                    <th className="px-5 py-3 font-medium">Creado</th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-slate-100">
                  {isLoadingTickets && (
                    <tr>
                      <td
                        colSpan={4}
                        className="px-5 py-8 text-center text-slate-500"
                      >
                        Cargando tickets...
                      </td>
                    </tr>
                  )}

                  {!isLoadingTickets && ticketsError && (
                    <tr>
                      <td
                        colSpan={4}
                        className="px-5 py-8 text-center text-rose-600"
                      >
                        No se pudieron cargar los tickets: {ticketsError}
                      </td>
                    </tr>
                  )}

                  {!isLoadingTickets &&
                    !ticketsError &&
                    tickets.length === 0 && (
                      <tr>
                        <td
                          colSpan={4}
                          className="px-5 py-8 text-center text-slate-500"
                        >
                          Todavía no tienes tickets.
                        </td>
                      </tr>
                    )}

                  {!isLoadingTickets &&
                    !ticketsError &&
                    tickets.map((ticket) => (
                      <tr
                        key={ticket.id}
                        className="transition hover:bg-slate-50/80"
                      >
                        <td className="px-5 py-4">
                          <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-indigo-100 text-xs font-semibold text-indigo-700">
                              {ticket.userEmail.slice(0, 2).toUpperCase()}
                            </div>

                            <div>
                              <p className="font-medium text-slate-800">
                                {ticket.title}
                              </p>
                              <p className="mt-1 text-xs text-slate-500">
                                {formatTicketId(ticket.id)} · {ticket.userEmail}
                              </p>
                            </div>
                          </div>
                        </td>

                        <td
                          className={`px-5 py-4 font-medium ${getPriorityStyle(getPriorityLabel(ticket.priority))}`}
                        >
                          {getPriorityLabel(ticket.priority)}
                        </td>

                        <td className="px-5 py-4">
                          <span
                            className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${getStatusStyle(getStatusLabel(ticket.status))}`}
                          >
                            {getStatusLabel(ticket.status)}
                          </span>
                        </td>

                        <td className="px-5 py-4 text-slate-500">
                          {new Date(ticket.createdAt).toLocaleDateString(
                            "es-ES",
                          )}
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>
            </div>

            <div className="border-t border-slate-200 px-5 py-4 text-xs text-slate-500">
              Mostrando {tickets.length} tickets
            </div>
          </section>

          <footer className="py-8 text-center text-xs text-slate-400">
            Helpdesk · Proyecto de práctica Full Stack
          </footer>

          {isNewTicketFormOpen && (
            <NewTicketForm
              onClose={() => setIsNewTicketFormOpen(false)}
              onSubmit={async (data) => {
                // Comprueba que existe una sesión antes de utilizar su token.
                if (!session) {
                  throw new Error("Debes iniciar sesión para crear un ticket.");
                }

                // Envía los datos al backend usando el token JWT.
                await createTicket(data, session.token);

                // Recupera la lista actualizada desde la API.
                const updatedTickets = await getMyTickets(session.token);
                setTickets(updatedTickets);

                // Cierra el formulario cuando todo ha terminado correctamente.
                setIsNewTicketFormOpen(false);
              }}
            />
          )}
        </div>
      </main>
    </div>
  );
}
