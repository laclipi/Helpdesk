
/* Centraliza las peticiones HTTP al backend para no repetir fetch en cada pantalla. */

const API_URL =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export type TicketPriority = "LOW" | "MEDIUM" | "HIGH";

export type TicketStatus =
  | "OPEN"
  | "IN_PROGRESS"
  | "RESOLVED"
  | "CLOSED";

export interface Ticket {
  id: number;
  title: string;
  description: string;
  priority: TicketPriority;
  status: TicketStatus;
  createdAt: string;
  userId: number;
  userEmail: string;
}

// Centraliza el manejo de errores y la conversión de respuestas JSON.
async function request<T>(
  path: string,
  options: RequestInit = {},
  token?: string,
): Promise<T> {
  const headers = new Headers(options.headers);

  if (options.body) {
    headers.set("Content-Type", "application/json");
  }

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
    cache: "no-store",
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Error HTTP ${response.status}`);
  }

  // DELETE puede responder sin contenido.
  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

// Inicia sesión y recibe el JWT.
export async function login(email: string, password: string) {
  return request<{ token: string }>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

// Registra un usuario nuevo.
export async function register(email: string, password: string) {
  return request<{ id: number; email: string; role: string }>(
    "/auth/register",
    {
      method: "POST",
      body: JSON.stringify({ email, password }),
    },
  );
}

// Obtiene los tickets del usuario autenticado.
export async function getMyTickets(token: string) {
  return request<Ticket[]>("/tickets", {}, token);
}

// Obtiene un ticket concreto.
export async function getTicket(id: number, token: string) {
  return request<Ticket>(`/tickets/${id}`, {}, token);
}

// Crea un ticket.
export async function createTicket(
  data: {
    title: string;
    description: string;
    priority: TicketPriority;
  },
  token: string,
) {
  return request<Ticket>(
    "/tickets",
    {
      method: "POST",
      body: JSON.stringify(data),
    },
    token,
  );
}

// Actualiza un ticket.
export async function updateTicket(
  id: number,
  data: {
    title: string;
    description: string;
    priority: TicketPriority;
  },
  token: string,
) {
  return request<Ticket>(
    `/tickets/${id}`,
    {
      method: "PUT",
      body: JSON.stringify(data),
    },
    token,
  );
}

// Elimina un ticket.
export async function deleteTicket(id: number, token: string) {
  return request<void>(
    `/tickets/${id}`,
    { method: "DELETE" },
    token,
  );
}