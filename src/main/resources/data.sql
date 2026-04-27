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

-- 5. 카테고리
INSERT INTO p_category (category_id, name, created_at, created_by)
VALUES ('550e8400-e29b-41d4-a716-446655440001', '치킨', NOW(), 'system');
INSERT INTO p_category (category_id, name, created_at, created_by)
VALUES ('550e8400-e29b-41d4-a716-446655440002', '피자', NOW(), 'system');

-- 6. 상점 (p_store)
INSERT INTO p_store (store_id, name, address, phone, average_rating, is_hidden, created_at, created_by)
VALUES ('550e8400-e29b-41d4-a716-446655440010', '똔똔치킨 강남점', '서울 강남구 역삼동 123-4', '02-123-4567', 4.5, false, NOW(), 'owner01');

INSERT INTO p_store (store_id, name, address, phone, average_rating, is_hidden, created_at, created_by)
VALUES ('550e8400-e29b-41d4-a716-446655440020', '도리도리피자', '서울 강남구 역삼동 567-8', '02-987-6543', 5.0, false, NOW(), 'owner02');

-- 7. 주문 (p_order)
-- 기존 주문 3개 (리뷰 있음)
INSERT INTO p_order (order_id, customer_id, store_id, total_price, status, order_type, created_at, created_by)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'customer01', '550e8400-e29b-41d4-a716-446655440010', 20000, 'COMPLETED', 'ONLINE', NOW(), 'customer01');

INSERT INTO p_order (order_id, customer_id, store_id, total_price, status, order_type, created_at, created_by)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', 'customer02', '550e8400-e29b-41d4-a716-446655440010', 18000, 'COMPLETED', 'ONLINE', NOW(), 'customer02');

INSERT INTO p_order (order_id, customer_id, store_id, total_price, status, order_type, created_at, created_by)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380c12', 'customer01', '550e8400-e29b-41d4-a716-446655440020', 25000, 'COMPLETED', 'ONLINE', NOW(), 'customer01');

-- 추가 주문 3개 (리뷰 없음, 테스트용)
INSERT INTO p_order (order_id, customer_id, store_id, total_price, status, order_type, created_at, created_by)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380d12', 'customer01', '550e8400-e29b-41d4-a716-446655440010', 15000, 'COMPLETED', 'ONLINE', NOW(), 'customer01');

INSERT INTO p_order (order_id, customer_id, store_id, total_price, status, order_type, created_at, created_by)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380e12', 'customer02', '550e8400-e29b-41d4-a716-446655440020', 30000, 'COMPLETED', 'ONLINE', NOW(), 'customer02');

INSERT INTO p_order (order_id, customer_id, store_id, total_price, status, order_type, created_at, created_by)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380f12', 'customer01', '550e8400-e29b-41d4-a716-446655440020', 12000, 'COMPLETED', 'ONLINE', NOW(), 'customer01');

-- 8. 리뷰 (p_review)
INSERT INTO p_review (review_id, order_id, store_id, customer_id, rating, content, created_at, created_by)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', '550e8400-e29b-41d4-a716-446655440010', 'customer01', 5, '치킨이 정말 똔똔하고 맛있어요! 겉바속촉의 정석입니다.', NOW(), 'customer01');

INSERT INTO p_review (review_id, order_id, store_id, customer_id, rating, content, created_at, created_by)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', '550e8400-e29b-41d4-a716-446655440010', 'customer02', 4, '굿굿!', NOW(), 'customer02');

INSERT INTO p_review (review_id, order_id, store_id, customer_id, rating, content, created_at, created_by)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380c12', '550e8400-e29b-41d4-a716-446655440020', 'customer01', 5, '피자 치즈가 장난 아니네요. 인생 피자 등극.', NOW(), 'customer01');
