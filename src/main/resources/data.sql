-- 1. 마스터 유저 (dongoan)
INSERT INTO p_user (username, nickname, email, password, role, is_public, created_at, created_by)
VALUES ('dongoan', '똔마스터', 'master@ldif.com', '$2a$10$zPKFwB3/qF3lqvxaY3mW1eqXk2B8v9waw452y/xh2EXEGr5lokCSG', 'MASTER', true, NOW(), 'system');

-- 2. 매니저 유저 (manager01)
INSERT INTO p_user (username, nickname, email, password, role, is_public, created_at, created_by)
VALUES ('manager01', '운영요원', 'manager@ldif.com', '$2a$10$zPKFwB3/qF3lqvxaY3mW1eqXk2B8v9waw452y/xh2EXEGr5lokCSG', 'MANAGER', true, NOW(), 'system');

-- 3. 사장님 유저 (owner01, owner02)
INSERT INTO p_user (username, nickname, email, password, role, is_public, created_at, created_by)
VALUES ('owner01', '치킨집사장', 'owner01@ldif.com', '$2a$10$zPKFwB3/qF3lqvxaY3mW1eqXk2B8v9waw452y/xh2EXEGr5lokCSG', 'OWNER', true, NOW(), 'system');
INSERT INTO p_user (username, nickname, email, password, role, is_public, created_at, created_by)
VALUES ('owner02', '피자집사장', 'owner02@ldif.com', '$2a$10$zPKFwB3/qF3lqvxaY3mW1eqXk2B8v9waw452y/xh2EXEGr5lokCSG', 'OWNER', true, NOW(), 'system');

-- 4. 손님 유저 (customer01, customer02)
INSERT INTO p_user (username, nickname, email, password, role, is_public, created_at, created_by)
VALUES ('customer01', '배달이좋아', 'customer01@ldif.com', '$2a$10$zPKFwB3/qF3lqvxaY3mW1eqXk2B8v9waw452y/xh2EXEGr5lokCSG', 'CUSTOMER', true, NOW(), 'system');
INSERT INTO p_user (username, nickname, email, password, role, is_public, created_at, created_by)
VALUES ('customer02', '오늘도배달', 'customer02@ldif.com', '$2a$10$zPKFwB3/qF3lqvxaY3mW1eqXk2B8v9waw452y/xh2EXEGr5lokCSG', 'CUSTOMER', true, NOW(), 'system');