
/* Centraliza las peticiones HTTP al backend para no repetir fetch en cada pantalla. */

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

// Define las prioridades que acepta el backend para los tickets.
export type TicketPriority = "LOW" | "MEDIUM" | "HIGH";

// Define los estados que puede tener un ticket.
export type TicketStatus = "OPEN" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";

// Representa la estructura de un ticket que devuelve la API.
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

  // Convierte los errores HTTP en mensajes que la interfaz pueda mostrar.
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Error HTTP ${response.status}`);
  }

  // DELETE puede responder sin contenido; evitamos intentar parsear JSON vacío.
  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

// Envía las credenciales y devuelve el JWT generado por Spring Security.
export async function login(email: string, password: string) {
  return request<{ token: string }>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

// Registra un usuario nuevo sin exponer su contraseña en la respuesta.
export async function register(email: string, password: string) {
  return request<{ id: number; email: string; role: string }>("/auth/register", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

// Recupera únicamente los tickets del usuario autenticado.
export async function getMyTickets(token: string) {
  return request<Ticket[]>("/tickets", {}, token);
}

// Recupera un ticket concreto; el backend verifica que pertenezca al usuario.
export async function getTicket(id: number, token: string) {
  return request<Ticket>(`/tickets/${id}`, {}, token);
}

// Crea un ticket con los datos que admite el backend.
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

// Actualiza los campos editables de un ticket.
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

// Elimina un ticket; el backend comprueba la propiedad antes de hacerlo.
export async function deleteTicket(id: number, token: string) {
  return request<void>(
    `/tickets/${id}`,
    {
      method: "DELETE",
    },
    token,
  );
}