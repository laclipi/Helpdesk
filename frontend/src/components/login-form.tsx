"use client";

import { useState } from "react";
import { login, register } from "@/lib/api";

interface LoginFormProps {
  onLogin: (token: string, email: string) => void;
}

export default function LoginForm({ onLogin }: LoginFormProps) {
  // Controla si mostramos el formulario de acceso o de registro.
  const [mode, setMode] = useState<"login" | "register">("login");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      if (mode === "register") {
        // Crea la cuenta; después el usuario podrá iniciar sesión.
        await register(email, password);
        setMode("login");
        setPassword("");
        setSuccess("Cuenta creada correctamente. Ya puedes iniciar sesión.");
      } else {
        // El acceso devuelve el JWT que necesita la aplicación.
        const result = await login(email, password);
        onLogin(result.token, email);
      }
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "No se ha podido completar la operación.",
      );
    } finally {
      setLoading(false);
    }
  }

  const isRegister = mode === "register";

  return (
    <main className="flex min-h-screen items-center justify-center px-5 py-12">
      {" "}
      <section className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-8 shadow-sm">
        {" "}
        <div className="mb-8 flex items-center gap-3">
          {" "}
          <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-blue-700 text-xl font-bold text-white">
            H{" "}
          </div>{" "}
          <div>
            {" "}
            <h1 className="text-xl font-bold text-slate-900">Helpdesk</h1>{" "}
            <p className="text-sm text-slate-500">Support center</p>{" "}
          </div>{" "}
        </div>
        <h2 className="text-2xl font-bold tracking-tight text-slate-900">
          {isRegister ? "Crear cuenta" : "Bienvenida de nuevo"}
        </h2>
        <p className="mt-2 text-sm text-slate-500">
          {isRegister
            ? "Regístrate para empezar a gestionar tus tickets."
            : "Inicia sesión para consultar tus tickets."}
        </p>
        <form className="mt-8 space-y-5" onSubmit={handleSubmit}>
          <div>
            <label
              htmlFor="email"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Correo electrónico
            </label>
            <input
              id="email"
              type="email"
              autoComplete="email"
              required
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="tu@email.com"
              className="w-full rounded-lg border border-slate-300 px-3 py-2.5 text-sm outline-none transition focus:border-blue-600 focus:ring-2 focus:ring-blue-100"
            />
          </div>

          <div>
            <label
              htmlFor="password"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Contraseña
            </label>
            <input
              id="password"
              type="password"
              autoComplete={isRegister ? "new-password" : "current-password"}
              required
              minLength={isRegister ? 8 : undefined}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Introduce tu contraseña"
              className="w-full rounded-lg border border-slate-300 px-3 py-2.5 text-sm outline-none transition focus:border-blue-600 focus:ring-2 focus:ring-blue-100"
            />
            {isRegister && (
              <p className="mt-1 text-xs text-slate-500">
                Mínimo 8 caracteres.
              </p>
            )}
          </div>

          {error && (
            <p
              role="alert"
              className="rounded-lg bg-rose-50 px-3 py-2.5 text-sm text-rose-700"
            >
              {error}
            </p>
          )}

          {success && (
            <p
              role="status"
              className="rounded-lg bg-emerald-50 px-3 py-2.5 text-sm text-emerald-700"
            >
              {success}
            </p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-blue-700 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-800 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading
              ? "Procesando..."
              : isRegister
                ? "Crear cuenta"
                : "Iniciar sesión"}
          </button>
        </form>
        <p className="mt-6 text-center text-sm text-slate-600">
          {isRegister ? "¿Ya tienes cuenta?" : "¿Todavía no tienes cuenta?"}{" "}
          <button
            type="button"
            onClick={() => {
              setMode(isRegister ? "login" : "register");
              setError("");
              setSuccess("");
            }}
            className="font-semibold text-blue-700 hover:text-blue-800"
          >
            {isRegister ? "Iniciar sesión" : "Crear cuenta"}
          </button>
        </p>
        <p className="mt-6 text-center text-xs leading-5 text-slate-400">
          Acceso protegido mediante autenticación JWT.
        </p>
      </section>
    </main>
  );
}
