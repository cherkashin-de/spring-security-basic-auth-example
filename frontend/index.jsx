import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";

export default function App() {
  const [page, setPage] = useState("login"); // login | register | home | users
  const [creatingUser, setCreatingUser] = useState(false);
  const [newUserForm, setNewUserForm] = useState({
    username: "",
    password: "",
    fullName: "",
    birthDay: "",
    salary: "",
  });
  const [login, setLogin] = useState("");
  const [password, setPassword] = useState("");
  const [userData, setUserData] = useState(null);
  const [users, setUsers] = useState([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [editableUsers, setEditableUsers] = useState({});

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
    birthDay: "",
    salary: "",
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
          username: data.username || data.userName || data.login || "",
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
      const payload = {
        username: profile.username && profile.username.trim().length > 0 ? profile.username : undefined,
        fullName: profile.fullName,
        birthDay: birthdayIso,
        salary: Number.isFinite(parsedSalary) ? parsedSalary : undefined,
      };

      const res = await fetch(`${API_URL}/users/information`, {
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
          username: (updated && (updated.username || updated.login)) || payload.username || (prev && prev.username) || "",
          fullName: (updated && updated.fullName) || profile.fullName || (prev && prev.fullName) || "",
          birthDay: (updated && (updated.birthDay || updated.birthday || updated.birthDate)) || birthdayIso || (prev && (prev.birthDay || prev.birthday || prev.birthDate)) || undefined,
          salary: (updated && updated.salary) ?? (Number.isFinite(parsedSalary) ? parsedSalary : (prev && prev.salary)),
          roles: (prev && prev.roles) || undefined,
        }));
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
        birthDay: birthdayValue,
        salary: (userData.salary ?? "").toString(),
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
    return roles.includes("ADMIN") || roles.includes("ADMIN_ROLE");
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
            email: u.email ?? null,
          }))
        : [];
      setUsers(normalized);
    } catch (e) {
      showToast("Ошибка загрузки: " + e, "error");
    } finally {
      setUsersLoading(false);
    }
  };

  useEffect(() => {
    if (page === "users") {
      if (!userData || !hasAdminRole()) {
        showToast("Доступ только для администратора", "error");
        setPage("home");
      } else {
        fetchUsers();
      }
    }
  }, [page]);

  useEffect(() => {
    if (!Array.isArray(users) || users.length === 0) {
      setEditableUsers({});
      return;
    }
    const prepared = {};
    users.forEach((u) => {
      let birthDayInput = "";
      if (u.birthDay) {
        try {
          const d = new Date(u.birthDay);
          birthDayInput = isNaN(d.getTime()) ? "" : d.toISOString().slice(0, 10);
        } catch (_) {
          birthDayInput = "";
        }
      }
      prepared[u.id] = {
        username: u.username ?? "",
        fullName: u.fullName ?? "",
        birthDay: birthDayInput,
        salary: u.salary ?? "",
      };
    });
    setEditableUsers(prepared);
  }, [users]);

  const onEditableChange = (userId, field, value) => {
    setEditableUsers((prev) => ({
      ...prev,
      [userId]: {
        ...(prev[userId] || {}),
        [field]: value,
      },
    }));
  };

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
                <p className="text-sm text-slate-500 mt-1">Управление пользователями доступно только администраторам</p>
              </div>
              <div className="flex gap-2">
                <motion.button
                  whileTap={{ scale: 0.98 }}
                  whileHover={{ scale: 1.02 }}
                  onClick={() => { setCreatingUser(true); setNewUserForm({ username: "", password: "", fullName: "", birthDay: "", salary: "" }); }}
                  className="px-4 py-2 rounded-lg bg-green-500 text-white hover:bg-green-600 shadow"
                >
                  Создать пользователя
                </motion.button>
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

            {creatingUser && (
              <div className="mb-6 bg-white rounded-xl shadow-lg border border-slate-200 p-6">
                <h3 className="text-xl font-bold mb-4">Создать нового пользователя</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm mb-1">Логин *</label>
                    <input
                      type="text"
                      className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                      value={newUserForm.username}
                      onChange={(e) => setNewUserForm({ ...newUserForm, username: e.target.value })}
                    />
                  </div>
                  <div>
                    <label className="block text-sm mb-1">Пароль *</label>
                    <input
                      type="password"
                      className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                      value={newUserForm.password}
                      onChange={(e) => setNewUserForm({ ...newUserForm, password: e.target.value })}
                    />
                  </div>
                  <div>
                    <label className="block text-sm mb-1">Полное имя</label>
                    <input
                      type="text"
                      className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                      value={newUserForm.fullName}
                      onChange={(e) => setNewUserForm({ ...newUserForm, fullName: e.target.value })}
                    />
                  </div>
                  <div>
                    <label className="block text-sm mb-1">Дата рождения</label>
                    <input
                      type="date"
                      className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                      value={newUserForm.birthDay}
                      onChange={(e) => setNewUserForm({ ...newUserForm, birthDay: e.target.value })}
                    />
                  </div>
                  <div>
                    <label className="block text-sm mb-1">Зарплата</label>
                    <input
                      type="number"
                      step="0.01"
                      className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400/70"
                      value={newUserForm.salary}
                      onChange={(e) => setNewUserForm({ ...newUserForm, salary: e.target.value })}
                    />
                  </div>
                </div>
                <div className="flex gap-2 mt-4">
                  <motion.button
                    whileTap={{ scale: 0.98 }}
                    whileHover={{ scale: 1.02 }}
                    onClick={async () => {
                      if (!newUserForm.username || !newUserForm.password) {
                        showToast("Логин и пароль обязательны", "error");
                        return;
                      }
                      try {
                        const parsedSalary = parseFloat(String(newUserForm.salary ?? "").replace(",", "."));
                        const birthdayIso = newUserForm.birthDay ? new Date(newUserForm.birthDay).toISOString() : undefined;
                        const payload = {
                          username: newUserForm.username,
                          password: newUserForm.password,
                          fullName: newUserForm.fullName || undefined,
                          birthDay: birthdayIso,
                          salary: Number.isFinite(parsedSalary) ? parsedSalary : undefined,
                        };
                        const res = await fetch(`${API_URL}/api/users`, {
                          method: "POST",
                          headers: {
                            "Content-Type": "application/json",
                            Authorization: "Basic " + btoa(`${login}:${password}`),
                          },
                          body: JSON.stringify(payload),
                        });
                        if (res.ok) {
                          showToast("Пользователь создан", "success");
                          setCreatingUser(false);
                          setNewUserForm({ username: "", password: "", fullName: "", birthDay: "", salary: "" });
                          fetchUsers();
                        } else if (res.status === 403 || res.status === 401) {
                          showToast("Нет прав для создания пользователя", "error");
                        } else if (res.status === 400) {
                          showToast("Некорректные данные", "error");
                        } else {
                          showToast("Не удалось создать пользователя", "error");
                        }
                      } catch (e) {
                        showToast("Ошибка создания: " + e, "error");
                      }
                    }}
                    className="px-4 py-2 rounded-lg bg-green-500 text-white hover:bg-green-600 shadow"
                  >
                    Создать
                  </motion.button>
                  <motion.button
                    whileTap={{ scale: 0.98 }}
                    whileHover={{ scale: 1.02 }}
                    onClick={() => { setCreatingUser(false); setNewUserForm({ username: "", password: "", fullName: "", birthDay: "", salary: "" }); }}
                    className="px-4 py-2 rounded-lg bg-slate-100 text-slate-800 hover:bg-slate-200 border"
                  >
                    Отмена
                  </motion.button>
                </div>
              </div>
            )}

            <div className="bg-white rounded-xl shadow-lg border border-slate-200 overflow-hidden flex-1 flex flex-col min-h-0">
              <div className="overflow-auto flex-1">
                <table className="min-w-full table-auto">
                  <thead className="sticky top-0 bg-slate-50/95 backdrop-blur border-b">
                    <tr className="text-left text-xs font-semibold text-slate-600">
                      <th className="px-4 py-3 w-[18%]">Логин</th>
                      <th className="px-4 py-3 w-[20%]">ФИО</th>
                      <th className="px-4 py-3 w-[12%]">Дата рождения</th>
                      <th className="px-4 py-3 w-[10%]">Зарплата</th>
                      <th className="px-4 py-3 w-[20%]">Роли</th>
                      <th className="px-4 py-3 w-[20%]">Действия</th>
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
                      users.map((u, idx) => {
                        const editable = editableUsers[u.id] || {
                          username: u.username ?? "",
                          fullName: u.fullName ?? "",
                          birthDay: (() => {
                            if (!u.birthDay) return "";
                            const d = new Date(u.birthDay);
                            return isNaN(d.getTime()) ? "" : d.toISOString().slice(0, 10);
                          })(),
                          salary: u.salary ?? "",
                        };
                        return (
                          <tr key={u.id} className={"border-b last:border-b-0 " + (idx % 2 === 0 ? "bg-white" : "bg-slate-50/50") }>
                            <td className="px-4 py-3 align-top">
                              <input
                                type="text"
                                className="w-full px-2 py-1.5 border rounded-lg bg-slate-50 text-sm"
                                value={editable.username}
                                readOnly
                              />
                            </td>
                            <td className="px-4 py-3 align-top">
                              <input
                                type="text"
                                className="w-full px-2 py-1.5 border rounded-lg text-sm"
                                value={editable.fullName}
                                onChange={(e) => onEditableChange(u.id, "fullName", e.target.value)}
                              />
                            </td>
                            <td className="px-4 py-3 align-top">
                              <input
                                type="date"
                                className="w-full px-2 py-1.5 border rounded-lg text-sm"
                                value={editable.birthDay || ""}
                                onChange={(e) => onEditableChange(u.id, "birthDay", e.target.value)}
                              />
                            </td>
                            <td className="px-4 py-3 align-top">
                              <input
                                type="number"
                                step="0.01"
                                className="w-full px-2 py-1.5 border rounded-lg text-sm"
                                value={editable.salary ?? ""}
                                onChange={(e) => onEditableChange(u.id, "salary", e.target.value)}
                              />
                            </td>
                          <td className="px-4 py-3 align-top">
                            <div className="flex flex-wrap gap-1">
                              {Array.isArray(u.roles) && u.roles.length > 0 ? (
                                u.roles.map((r) => (
                                  <span key={r} className="inline-flex items-center px-2 py-0.5 rounded-full text-xs bg-indigo-50 text-indigo-700 border border-indigo-200">
                                    {r}
                                  </span>
                                ))
                              ) : (
                                <span className="text-xs text-slate-400">Нет ролей</span>
                              )}
                            </div>
                          </td>
                            <td className="px-4 py-3 align-top text-right">
                            <div className="flex gap-2">
                              <motion.button
                                whileTap={{ scale: 0.98 }}
                                whileHover={{ scale: 1.02 }}
                                className="px-3 py-2 rounded-lg bg-emerald-500 text-white hover:bg-emerald-600 shadow text-xs"
                                onClick={async () => {
                                  try {
                                    const parsedSalary = parseFloat(String(editable.salary ?? "").toString().replace(",", "."));
                                    const birthdayIso = editable.birthDay ? new Date(editable.birthDay).toISOString() : undefined;
                                    const payload = {
                                      username: editable.username?.trim() || u.username,
                                      fullName: editable.fullName || undefined,
                                      birthDay: birthdayIso,
                                      salary: Number.isFinite(parsedSalary) ? parsedSalary : undefined,
                                    };
                                    const res = await fetch(`${API_URL}/api/users`, {
                                      method: "PUT",
                                      headers: {
                                        "Content-Type": "application/json",
                                        Authorization: "Basic " + btoa(`${login}:${password}`),
                                      },
                                      body: JSON.stringify(payload),
                                    });
                                    if (res.ok) {
                                      showToast("Пользователь обновлён", "success");
                                      fetchUsers();
                                    } else if (res.status === 403 || res.status === 401) {
                                      showToast("Нет прав для изменения пользователя", "error");
                                    } else {
                                      showToast("Не удалось обновить пользователя", "error");
                                    }
                                  } catch (e) {
                                    showToast("Ошибка сохранения: " + e, "error");
                                  }
                                }}
                              >
                                Обновить
                              </motion.button>
                              <motion.button
                                whileTap={{ scale: 0.98 }}
                                whileHover={{ scale: 1.02 }}
                                className="px-3 py-2 rounded-lg bg-rose-500 text-white hover:bg-rose-600 shadow text-xs"
                                onClick={async () => {
                                  if (!confirm(`Удалить пользователя ${u.username}?`)) return;
                                  try {
                                    const res = await fetch(`${API_URL}/api/users?username=${encodeURIComponent(u.username)}`, {
                                      method: "DELETE",
                                      headers: {
                                        Authorization: "Basic " + btoa(`${login}:${password}`),
                                      },
                                    });
                                    if (res.ok) {
                                      showToast("Пользователь удалён", "success");
                                      fetchUsers();
                                    } else if (res.status === 403 || res.status === 401) {
                                      showToast("Нет прав для удаления пользователя", "error");
                                    } else if (res.status === 404) {
                                      showToast("Пользователь не найден", "error");
                                    } else {
                                      showToast("Не удалось удалить пользователя", "error");
                                    }
                                  } catch (e) {
                                    showToast("Ошибка удаления: " + e, "error");
                                  }
                                }}
                              >
                                Удалить
                              </motion.button>
                            </div>
                          </td>
                          </tr>
                        );
                      })
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
