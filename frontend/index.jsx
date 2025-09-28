import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";

export default function App() {
  const [page, setPage] = useState("login"); // login | register | home | users
  const [login, setLogin] = useState("");
  const [password, setPassword] = useState("");
  const [userData, setUserData] = useState(null);
  const [users, setUsers] = useState([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [allRoles, setAllRoles] = useState([]);
  const [selectedRolesByUser, setSelectedRolesByUser] = useState({});
  const [rolesDropdownOpenForUser, setRolesDropdownOpenForUser] = useState(null);

  const [form, setForm] = useState({
    username: "",
    password: "",
    fullName: "",
    birthDay: "",
    salary: "",
  });

  const [profile, setProfile] = useState({
    username: "",
    fullName: "",
    password: "",
    birthDay: "",
    salary: "",
    roles: "",
  });

  const [toasts, setToasts] = useState([]);

  const showToast = (message, type = "info", duration = 3000) => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, duration);
  };

  const API_URL = (typeof process !== "undefined" && process.env && process.env.REACT_APP_API_URL) || "http://localhost:8080";

const handleLogin = async () => {
  try {
    const res = await fetch(`${API_URL}/api/users/me`, {
      method: "GET",
      headers: {
        Authorization: "Basic " + btoa(`${login}:${password}`),
      },
    });

    if (res.ok) {
      const contentType = res.headers.get("content-type") || "";
      const contentLength = res.headers.get("content-length");
      let data = null;
      try {
        if (
          contentType.includes("application/json") &&
          contentLength !== "0"
        ) {
          data = await res.json();
        } else {
          const text = await res.text();
          if (text && text.trim().length > 0) {
            data = JSON.parse(text);
          }
        }
      } catch (_) {
        // ignore JSON parse errors for empty/non-JSON bodies
      }

      if (data) {
        const normalized = {
          ...data,
          username: data.username || data.userName || "",
          birthDay: data.birthDay || data.birthday || data.birthDate || "",
          roles: Array.isArray(data.roles)
            ? data.roles
            : typeof data.roles === "string"
            ? data.roles.split(",").map((r) => r.trim()).filter(Boolean)
            : [],
        };
        setUserData(normalized);
      }
      setPage("home");
    } else {
      showToast("Неверный логин или пароль", "error");
    }
  } catch (err) {
    showToast("Ошибка соединения: " + err, "error");
  }
};

const handleRegister = async () => {
  try {
    const parsedSalary = parseFloat(String(form.salary ?? "").replace(",", "."));
    const birthdayIso = form.birthDay ? new Date(form.birthDay).toISOString() : null;
    const payload = {
      username: form.username,
      password: form.password,
      fullName: form.fullName,
      birthDay: birthdayIso,
      salary: Number.isFinite(parsedSalary) ? parsedSalary : null,
    };

    const res = await fetch(`${API_URL}/api/public/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      showToast("Регистрация успешна!", "success");
      setPage("login");
    } else {
      showToast("Ошибка регистрации", "error");
    }
  } catch (err) {
    showToast("Ошибка соединения: " + err, "error");
  }
};


  const handleLogout = () => {
    setUserData(null);
    setLogin("");
    setPassword("");
    setPage("login");
  };

  const handleUpdateProfile = async () => {
    try {
      const parsedSalary = parseFloat(String(profile.salary ?? "").replace(",", "."));
      const birthdayIso = profile.birthDay ? new Date(profile.birthDay).toISOString() : undefined;
      const rolesArray = (profile.roles || "")
        .split(",")
        .map((r) => r.trim())
        .filter((r) => r.length > 0);
      const payload = {
        username: profile.username && profile.username.trim().length > 0 ? profile.username : undefined,
        fullName: profile.fullName,
        birthDay: birthdayIso,
        salary: Number.isFinite(parsedSalary) ? parsedSalary : undefined,
        roles: rolesArray.length > 0 ? rolesArray : undefined,
        password: profile.password && profile.password.trim().length > 0 ? profile.password : undefined,
      };

      const res = await fetch(`${API_URL}/api/user/me`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Basic " + btoa(`${login}:${password}`),
        },
        body: JSON.stringify(payload),
      });

      if (res.ok) {
        // попытка прочесть JSON, если есть
        const contentType = res.headers.get("content-type") || "";
        let updated = null;
        try {
          if (contentType.includes("application/json")) {
            updated = await res.json();
          }
        } catch (_) {}

        setUserData((prev) => ({
          ...(prev || {}),
          username: (updated && updated.username) || payload.username || (prev && prev.username) || "",
          fullName: (updated && updated.fullName) || profile.fullName || (prev && prev.fullName) || "",
          birthDay: (updated && (updated.birthDay || updated.birthday || updated.birthDate)) || birthdayIso || (prev && (prev.birthDay || prev.birthday || prev.birthDate)) || undefined,
          salary: (updated && updated.salary) ?? (Number.isFinite(parsedSalary) ? parsedSalary : (prev && prev.salary)),
          roles: (updated && updated.roles) || (rolesArray.length > 0 ? rolesArray : (prev && prev.roles)) || undefined,
        }));
        if (profile.password && profile.password.trim().length > 0) {
          setPassword(profile.password);
        }
        setProfile((p) => ({ ...p, password: "" }));
        showToast("Изменения сохранены", "success");
      } else {
        showToast("Не удалось сохранить изменения", "error");
      }
    } catch (err) {
      showToast("Ошибка соединения: " + err, "error");
    }
  };

  useEffect(() => {
    if (userData) {
      let birthdayValue = "";
      const raw = userData.birthDay || userData.birthday || userData.birthDate;
      if (raw) {
        try {
          const d = new Date(raw);
          birthdayValue = isNaN(d.getTime()) ? "" : d.toISOString().slice(0, 10);
        } catch (_) {}
      }
      setProfile({
        username: userData.username || "",
        fullName: userData.fullName || "",
        password: "",
        birthDay: birthdayValue,
        salary: (userData.salary ?? "").toString(),
        roles: Array.isArray(userData.roles) ? userData.roles.join(", ") : (userData.roles || ""),
      });
    }
  }, [userData]);

  useEffect(() => {
    if (page === "login") {
      setLogin("");
      setPassword("");
    }
    if (page === "register") {
      setForm({ username: "", password: "", fullName: "", birthDay: "", salary: "" });
    }
  }, [page]);

  // удалены список пользователей и отдельная страница редактирования

  const hasAdminRole = () => {
    const roles = Array.isArray(userData?.roles)
      ? userData.roles
      : typeof userData?.roles === "string"
      ? userData.roles.split(",").map((r) => r.trim())
      : [];
    return roles.includes("ADMIN_ROLE");
  };

  const fetchUsers = async () => {
    setUsersLoading(true);
    try {
      const res = await fetch(`${API_URL}/api/users`, {
        method: "GET",
        headers: {
          Authorization: "Basic " + btoa(`${login}:${password}`),
        },
      });
      if (!res.ok) {
        if (res.status === 403 || res.status === 401) {
          showToast("Недостаточно прав для просмотра пользователей", "error");
          setUsers([]);
        } else {
          showToast("Не удалось загрузить пользователей", "error");
        }
        setUsersLoading(false);
        return;
      }
      const data = await res.json();
      const normalized = Array.isArray(data)
        ? data.map((u) => ({
            id: u.login ?? u.username ?? u.userId ?? u.id,
            username: u.login ?? u.username ?? "",
            fullName: u.fullName ?? "",
            birthDay: u.birthDay || u.birthday || u.birthDate || null,
            salary: u.salary ?? null,
            roles: Array.isArray(u.roles)
              ? u.roles
              : typeof u.roles === "string"
              ? u.roles.split(",").map((r) => r.trim()).filter(Boolean)
              : [],
          }))
        : [];
      setUsers(normalized);
    } catch (e) {
      showToast("Ошибка загрузки: " + e, "error");
    } finally {
      setUsersLoading(false);
    }
  };

  const fetchRoles = async () => {
    try {
      const res = await fetch(`${API_URL}/api/public/roles`, { method: "GET" });
      if (!res.ok) {
        showToast("Не удалось загрузить роли", "error");
        return;
      }
      const data = await res.json();
      const roles = Array.isArray(data)
        ? data.map((r) => (typeof r === "string" ? r : r?.name || r?.code)).filter(Boolean)
        : [];
      setAllRoles(roles);
    } catch (e) {
      showToast("Ошибка загрузки ролей: " + e, "error");
    }
  };

  useEffect(() => {
    if (page === "users") {
      if (!userData || !hasAdminRole()) {
        showToast("Доступ только для администратора", "error");
        setPage("home");
      } else {
        fetchRoles();
        fetchUsers();
      }
    }
  }, [page]);

  useEffect(() => {
    if (page === "users") {
      const initial = {};
      users.forEach((u) => {
        initial[u.id] = Array.isArray(u.roles)
          ? u.roles
          : typeof u.roles === "string"
          ? u.roles.split(",").map((r) => r.trim()).filter(Boolean)
          : [];
      });
      setSelectedRolesByUser(initial);
    }
  }, [users, page]);

  useEffect(() => {
    const onDocClick = () => setRolesDropdownOpenForUser(null);
    if (rolesDropdownOpenForUser !== null) {
      document.addEventListener("click", onDocClick);
      return () => document.removeEventListener("click", onDocClick);
    }
  }, [rolesDropdownOpenForUser]);

  return (
    <div className="relative min-h-screen bg-white">
      <motion.div
        className={(page === "home" || page === "users") ? "relative w-full h-screen px-6 lg:px-10 py-6 lg:py-8 flex flex-col" : "relative bg-white/90 backdrop-blur rounded-2xl shadow-2xl p-8 w-[28rem] border border-white/20 mx-auto my-10"}
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
      >
        {page === "login" && (
          <div>
            <h2 className="text-2xl font-extrabold text-center mb-6 tracking-tight">Вход</h2>
            <input
              type="text"
              placeholder="Логин"
              className="w-full p-3 mb-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
              value={login}
              onChange={(e) => setLogin(e.target.value)}
            />
            <input
              type="password"
              placeholder="Пароль"
              className="w-full p-3 mb-4 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <motion.button
              whileTap={{ scale: 0.98 }}
              whileHover={{ scale: 1.01 }}
              onClick={handleLogin}
              className="w-full bg-indigo-500 text-white py-3 rounded-lg hover:bg-indigo-600 shadow-md"
            >
              Войти
            </motion.button>
            <p
              className="mt-4 text-sm text-center text-indigo-600 cursor-pointer"
              onClick={() => setPage("register")}
            >
              Регистрация
            </p>
          </div>
        )}

        {page === "register" && (
          <div>
            <h2 className="text-2xl font-extrabold text-center mb-6 tracking-tight">Регистрация</h2>
            <div className="space-y-3">
              <div>
                <label className="block text-sm mb-1">Логин</label>
                <input
                  type="text"
                  placeholder="Введите логин"
                  className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                  value={form.username}
                  onChange={(e) => setForm({ ...form, username: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm mb-1">Пароль</label>
                <input
                  type="password"
                  placeholder="Введите пароль"
                  className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                  value={form.password}
                  onChange={(e) => setForm({ ...form, password: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm mb-1">Полное имя</label>
                <input
                  type="text"
                  placeholder="Иван Иванов"
                  className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                  value={form.fullName}
                  onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm mb-1">Дата рождения</label>
                <input
                  type="date"
                  className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                  value={form.birthDay}
                  onChange={(e) => setForm({ ...form, birthDay: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm mb-1">Зарплата</label>
                <input
                  type="number"
                  step="0.01"
                  placeholder="0.00"
                  className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                  value={form.salary}
                  onChange={(e) => setForm({ ...form, salary: e.target.value })}
                />
              </div>
            </div>
            <motion.button
              whileTap={{ scale: 0.98 }}
              whileHover={{ scale: 1.01 }}
              onClick={handleRegister}
              className="w-full mt-4 bg-green-500 text-white py-3 rounded-lg hover:bg-green-600 shadow-md"
            >
              Зарегистрироваться
            </motion.button>
            <p
              className="mt-4 text-sm text-center text-indigo-600 cursor-pointer"
              onClick={() => setPage("login")}
            >
              Уже есть аккаунт? Войти
            </p>
          </div>
        )}

        {page === "home" && userData && (
          <div>
            <h2 className="text-2xl font-extrabold text-center mb-6">Профиль</h2>
            <div className="grid grid-cols-1 gap-3 text-sm">
              <div className="p-3 rounded-lg border bg-white/60">
                <span className="font-semibold">Логин: </span>{userData.username}
              </div>
              <div className="p-3 rounded-lg border bg-white/60">
                <span className="font-semibold">ФИО: </span>{userData.fullName}
              </div>
              <div className="p-3 rounded-lg border bg-white/60">
                <span className="font-semibold">Дата рождения: </span>{(() => { const raw = userData.birthDay; if(!raw) return ""; const d = new Date(raw); return isNaN(d.getTime()) ? "" : d.toISOString().slice(0,10); })()}
              </div>
              <div className="p-3 rounded-lg border bg-white/60">
                <span className="font-semibold">Зарплата: </span>{userData.salary}
              </div>
              {hasAdminRole() && (
                <div className="p-3 rounded-lg border bg-white/60">
                  <span className="font-semibold">Роли: </span>{Array.isArray(userData.roles) ? userData.roles.join(", ") : userData.roles}
                </div>
              )}
            </div>
            {hasAdminRole() && (
              <motion.button
                whileTap={{ scale: 0.98 }}
                whileHover={{ scale: 1.02 }}
                onClick={() => setPage("users")}
                className="mt-4 w-full px-4 py-2 rounded-lg bg-indigo-500 text-white hover:bg-indigo-600 shadow"
              >
                Перейти к пользователям (ADMIN)
              </motion.button>
            )}
            <motion.button
              whileTap={{ scale: 0.98 }}
              whileHover={{ scale: 1.02 }}
              onClick={handleLogout}
              className="mt-4 w-full px-4 py-2 rounded-lg bg-rose-500 text-white hover:bg-rose-600 shadow"
            >
              Выйти
            </motion.button>
          </div>
        )}

        {page === "users" && userData && hasAdminRole() && (
          <div className="flex-1 flex flex-col max-w-7xl mx-auto w-full min-h-0">
            <div className="flex items-start justify-between mb-6">
              <div>
                <h2 className="text-3xl font-extrabold tracking-tight">Пользователи</h2>
                <p className="text-sm text-slate-500 mt-1">Управление ролями доступно только администраторам</p>
              </div>
              <div className="flex gap-2">
                <motion.button
                  whileTap={{ scale: 0.98 }}
                  whileHover={{ scale: 1.02 }}
                  onClick={() => { fetchUsers(); }}
                  className="px-4 py-2 rounded-lg bg-indigo-500 text-white hover:bg-indigo-600 shadow"
                >
                  Обновить
                </motion.button>
                <motion.button
                  whileTap={{ scale: 0.98 }}
                  whileHover={{ scale: 1.02 }}
                  onClick={() => setPage("home")}
                  className="px-4 py-2 rounded-lg bg-slate-100 text-slate-800 hover:bg-slate-200 border"
                >
                  Назад
                </motion.button>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-lg border border-slate-200 overflow-hidden flex-1 flex flex-col min-h-0">
              <div className="overflow-auto flex-1">
                <table className="min-w-full table-auto">
                  <thead className="sticky top-0 bg-slate-50/95 backdrop-blur border-b">
                    <tr className="text-left text-xs font-semibold text-slate-600">
                      <th className="px-4 py-3 w-[24%]">Логин</th>
                      <th className="px-4 py-3 w-[26%]">ФИО</th>
                      <th className="px-4 py-3 w-[14%]">Дата рождения</th>
                      <th className="px-4 py-3 w-[12%]">Зарплата</th>
                      <th className="px-4 py-3 w-[18%]">Роли</th>
                      <th className="px-4 py-3 w-[6%]"></th>
                    </tr>
                  </thead>
                  <tbody className="text-sm">
                    {usersLoading ? (
                      <tr>
                        <td colSpan={6} className="px-4 py-6 text-center text-slate-500">Загрузка...</td>
                      </tr>
                    ) : users.length === 0 ? (
                      <tr>
                        <td colSpan={6} className="px-4 py-6 text-center text-slate-500">Нет данных</td>
                      </tr>
                    ) : (
                      users.map((u, idx) => (
                        <tr key={u.id} className={"border-b last:border-b-0 " + (idx % 2 === 0 ? "bg-white" : "bg-slate-50/50") }>
                          <td className="px-4 py-3 align-top">
                            <div className="font-medium text-slate-900 truncate" title={u.username}>{u.username}</div>
                          </td>
                          <td className="px-4 py-3 align-top">
                            <div className="truncate max-w-[18rem]">{u.fullName}</div>
                          </td>
                          <td className="px-4 py-3 align-top">
                            {(() => { const raw = u.birthDay; if(!raw) return ""; const d = new Date(raw); return isNaN(d.getTime()) ? "" : d.toISOString().slice(0,10); })()}
                          </td>
                          <td className="px-4 py-3 align-top">{u.salary ?? ""}</td>
                          <td className="px-4 py-3 align-top">
                            <div className="relative">
                              <div
                                className="min-h-[40px] w-full max-w-xs border rounded-lg p-1.5 flex items-center gap-1 cursor-pointer hover:bg-slate-50"
                                onClick={(e) => { e.stopPropagation(); setRolesDropdownOpenForUser(rolesDropdownOpenForUser === u.id ? null : u.id); }}
                              >
                                {(() => {
                                  const sel = selectedRolesByUser[u.id] || [];
                                  if (sel.length === 0) return <span className="text-xs text-slate-400">Выберите роли</span>;
                                  if (sel.length <= 2) return sel.map((r) => (
                                    <span key={r} className="inline-flex items-center px-2 py-0.5 rounded-full text-xs bg-indigo-50 text-indigo-700 border border-indigo-200">{r}</span>
                                  ));
                                  return <span className="text-xs text-slate-600">{sel.length} ролей выбрано</span>;
                                })()}
                              </div>
                              {rolesDropdownOpenForUser === u.id && (
                                <div className="absolute left-0 right-0 z-10 mt-2 bg-white border rounded-lg shadow-lg p-2" onClick={(e) => e.stopPropagation()}>
                                  <div className="max-h-60 overflow-auto pr-1">
                                    {allRoles.map((r) => {
                                      const checked = (selectedRolesByUser[u.id] || []).includes(r);
                                      return (
                                        <label key={r} className="flex items-center gap-2 py-1 px-2 rounded hover:bg-slate-50 cursor-pointer text-xs">
                                          <input
                                            type="checkbox"
                                            className="accent-indigo-600"
                                            checked={checked}
                                            onChange={(e) => {
                                              setSelectedRolesByUser((prev) => {
                                                const current = new Set(prev[u.id] || []);
                                                if (e.target.checked) current.add(r); else current.delete(r);
                                                return { ...prev, [u.id]: Array.from(current) };
                                              });
                                            }}
                                          />
                                          <span>{r}</span>
                                        </label>
                                      );
                                    })}
                                  </div>
                                  <div className="flex justify-end gap-2 pt-2 border-t mt-2">
                                    <button className="text-xs px-3 py-1.5 rounded border hover:bg-slate-50" onClick={() => setRolesDropdownOpenForUser(null)}>Готово</button>
                                  </div>
                                </div>
                              )}
                            </div>
                          </td>
                          <td className="px-4 py-3 align-top text-right">
                            <motion.button
                              whileTap={{ scale: 0.98 }}
                              whileHover={{ scale: 1.02 }}
                              className="px-3 py-2 rounded-lg bg-emerald-500 text-white hover:bg-emerald-600 shadow"
                              onClick={async () => {
                                try {
                                  const body = { roles: selectedRolesByUser[u.id] || [] };
                                  const res = await fetch(`${API_URL}/api/users/${encodeURIComponent(u.username)}/roles`, {
                                    method: "PUT",
                                    headers: {
                                      "Content-Type": "application/json",
                                      Authorization: "Basic " + btoa(`${login}:${password}`),
                                    },
                                    body: JSON.stringify(body),
                                  });
                                  if (res.ok) {
                                    showToast("Роли обновлены", "success");
                                    fetchUsers();
                                  } else if (res.status === 403 || res.status === 401) {
                                    showToast("Нет прав для изменения ролей", "error");
                                  } else {
                                    showToast("Не удалось сохранить роли", "error");
                                  }
                                } catch (e) {
                                  showToast("Ошибка сохранения: " + e, "error");
                                }
                              }}
                            >
                              Сохранить
                            </motion.button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {page === "edit" && userData && null}
      </motion.div>

      <div className="fixed bottom-4 right-4 z-50 space-y-2">
        <AnimatePresence>
          {toasts.map((t) => (
            <motion.div
              key={t.id}
              initial={{ opacity: 0, x: 50 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 50 }}
              className={
                "min-w-[16rem] max-w-sm px-4 py-3 rounded-lg shadow-lg border " +
                (t.type === "success"
                  ? "bg-emerald-50 border-emerald-200 text-emerald-800"
                  : t.type === "error"
                  ? "bg-rose-50 border-rose-200 text-rose-800"
                  : "bg-slate-50 border-slate-200 text-slate-800")
              }
            >
              {t.message}
            </motion.div>
          ))}
        </AnimatePresence>
      </div>
    </div>
  );
}
