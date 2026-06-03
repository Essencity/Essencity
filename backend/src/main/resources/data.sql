-- Essencity 测试数据
-- 密码统一为 "123456"（BCrypt 编码）
-- 导入前请确保已执行 schema.sql

USE xiaohongshu;

-- ============================================================
-- 1. 用户数据（5 个测试用户）
-- ============================================================
INSERT INTO users (username, password, nickname, avatar, bio, gender) VALUES
('xiaohong',  '$2b$10$dHu7ior0qnNg/sNJr78MSOaehJcFsHVjjGSZWj/Tbv4.cPiTR8Cz.', '小红爱生活', '/uploads/default-avatar.png', '分享生活的美好瞬间 | 美食达人 | 旅行爱好者', '女'),
('meishi',    '$2b$10$dHu7ior0qnNg/sNJr78MSOaehJcFsHVjjGSZWj/Tbv4.cPiTR8Cz.', '美食家小王', '/uploads/default-avatar.png', '每天分享一道家常菜 | 厨房是我的快乐天地', '男'),
('travel',    '$2b$10$dHu7ior0qnNg/sNJr78MSOaehJcFsHVjjGSZWj/Tbv4.cPiTR8Cz.', '背包客小李', '/uploads/default-avatar.png', '走遍千山万水 | 用镜头记录世界', '男'),
('fashion',   '$2b$10$dHu7ior0qnNg/sNJr78MSOaehJcFsHVjjGSZWj/Tbv4.cPiTR8Cz.', '时尚达人Lucy', '/uploads/default-avatar.png', '穿搭是一种态度 | 时尚博主 | 合作私信', '女'),
('fitness',   '$2b$10$dHu7ior0qnNg/sNJr78MSOaehJcFsHVjjGSZWj/Tbv4.cPiTR8Cz.', '健身教练阿杰', '/uploads/default-avatar.png', '自律给我自由 | 健身8年 | 科学健身', '男');

-- ============================================================
-- 2. 帖子数据（15 个测试帖子）
-- ============================================================
INSERT INTO posts (title, description, type, url, cover_url, author_id, tag, created_at) VALUES
-- 美食家小王（author_id=2）的帖子
('超好吃的红烧肉做法',
 '今天分享一道家常红烧肉，肥而不腻，入口即化！\n\n材料：五花肉500g、冰糖、生抽、老抽、料酒、八角、桂皮\n\n做法：\n1. 五花肉切块焯水\n2. 锅中放油炒糖色\n3. 放入肉块翻炒上色\n4. 加调料和热水炖1小时\n5. 大火收汁即可',
 'image', '/uploads/test/food-1.jpg', '/uploads/test/food-1.jpg', 2, '美食', NOW() - INTERVAL 15 DAY),

('自制奶茶比外面好喝',
 '再也不用排队买奶茶了！自制珍珠奶茶，简单又好喝～\n\n材料：红茶包、牛奶、黑糖、木薯粉\n\n步骤超简单，跟着视频学起来！',
 'image', '/uploads/test/food-2.jpg', '/uploads/test/food-2.jpg', 2, '美食', NOW() - INTERVAL 14 DAY),

('周末早午餐brunch',
 '周末睡到自然醒，给自己做一份精致的早午餐\n\n法式吐司+牛油果+煎蛋+水果\n\n生活需要仪式感',
 'image', '/uploads/test/food-3.jpg', '/uploads/test/food-3.jpg', 2, '美食', NOW() - INTERVAL 13 DAY),

-- 小红爱生活（author_id=1）的美食帖
('虾滑时蔬水晶饺',
 '颜值超高又好吃的水晶饺！\n\n馅料：虾滑+胡萝卜+玉米+青豆\n皮：澄面+淀粉\n\n蒸10分钟就好啦，晶莹剔透超好看',
 'image', '/uploads/test/food-4.jpg', '/uploads/test/food-4.jpg', 1, '美食', NOW() - INTERVAL 12 DAY),

-- 时尚达人Lucy（author_id=4）的帖子
('秋冬穿搭合集',
 '整理了最近的秋冬穿搭，每一套都好爱！\n\n风格：简约通勤风\n色系：大地色系为主\n\n姐妹们觉得哪套最好看？',
 'image', '/uploads/test/fashion-1.jpg', '/uploads/test/fashion-1.jpg', 4, '穿搭', NOW() - INTERVAL 11 DAY),

('小个子女生穿搭技巧',
 '155cm的我终于找到了显高的穿搭秘诀！\n\n1. 高腰线是关键\n2. 同色系搭配拉长比例\n3. 尖头鞋显腿长\n4. 适当露肤更显高',
 'image', '/uploads/test/fashion-2.jpg', '/uploads/test/fashion-2.jpg', 4, '穿搭', NOW() - INTERVAL 10 DAY),

('这件大衣太好看了',
 '新入的驼色大衣，质感超好！\n\n搭配白色高领毛衣+黑色阔腿裤\n简单又有气质\n\n#穿搭分享 #秋冬穿搭',
 'image', '/uploads/test/fashion-3.jpg', '/uploads/test/fashion-3.jpg', 4, '穿搭', NOW() - INTERVAL 9 DAY),

-- 背包客小李（author_id=3）的帖子
('云南大理旅行攻略',
 '终于去了心心念念的大理！\n\n行程安排：\nDay1: 大理古城-人民路-洋人街\nDay2: 洱海环湖骑行\nDay3: 苍山索道-寂照庵\nDay4: 喜洲古镇-海舌公园\n\n详细攻略看图～',
 'image', '/uploads/test/travel-1.jpg', '/uploads/test/travel-1.jpg', 3, '旅行', NOW() - INTERVAL 8 DAY),

('日落时分的洱海',
 '在洱海边看了此生最美的日落\n\n金色的阳光洒在湖面上\n远处的苍山若隐若现\n\n这一刻，觉得所有的奔波都值得',
 'image', '/uploads/test/travel-2.jpg', '/uploads/test/travel-2.jpg', 3, '旅行', NOW() - INTERVAL 7 DAY),

('一个人的背包旅行',
 '第一次一个人背包旅行，去了厦门\n\n在鼓浪屿迷路了三次\n在曾厝垵吃了一整条街\n在环岛路骑了一下午的车\n\n一个人旅行，是和自己的对话',
 'image', '/uploads/test/travel-3.jpg', '/uploads/test/travel-3.jpg', 3, '旅行', NOW() - INTERVAL 6 DAY),

-- 健身教练阿杰（author_id=5）的帖子
('新手健身入门指南',
 '很多小伙伴问我新手该怎么开始健身\n\n给大家整理了一份入门计划：\n\n周一：胸+三头\n周二：背+二头\n周三：休息\n周四：肩+核心\n周五：腿\n周末：有氧\n\n每个动作3组，每组12次',
 'image', '/uploads/test/fitness-1.jpg', '/uploads/test/fitness-1.jpg', 5, '健身', NOW() - INTERVAL 5 DAY),

('30天腹肌挑战',
 '跟着这个计划练30天，腹肌真的会出来！\n\n每天15分钟：\n- 卷腹 3x20\n- 平板支撑 3x45秒\n- 俄罗斯转体 3x20\n- 登山者 3x20\n\n坚持就是胜利！',
 'image', '/uploads/test/fitness-2.jpg', '/uploads/test/fitness-2.jpg', 5, '健身', NOW() - INTERVAL 4 DAY),

('健身餐这样吃',
 '健身三分练七分吃！\n\n分享我的健身餐：\n早餐：燕麦+蛋白+香蕉\n午餐：鸡胸肉+糙米+西兰花\n晚餐：三文鱼+红薯+沙拉\n\n蛋白质摄入量 = 体重(kg) x 1.5g',
 'image', '/uploads/test/fitness-3.jpg', '/uploads/test/fitness-3.jpg', 5, '健身', NOW() - INTERVAL 3 DAY),

-- 小红爱生活（author_id=1）的生活帖
('我的房间改造记录',
 '花了一个周末改造了我的小房间\n\n改造前：杂乱无章\n改造后：温馨小窝\n\n花费不到500块，效果超满意！\n\n#房间改造 #租房改造',
 'image', '/uploads/test/life-1.jpg', '/uploads/test/life-1.jpg', 1, '家居', NOW() - INTERVAL 2 DAY),

('周末vlog',
 '记录一下美好的周末时光\n\n睡到自然醒 → 做早餐 → 逛花市 → 下午茶 → 看电影\n\n平凡的日子也要好好过',
 'video', '/uploads/test/life-2.jpg', '/uploads/test/life-2.jpg', 1, '影视', NOW() - INTERVAL 1 DAY);

-- ============================================================
-- 3. 评论数据（每个帖子 3 条评论，共 45 条）
-- ============================================================
INSERT INTO comments (post_id, user_id, content, parent_id, created_at) VALUES
-- 帖子1: 红烧肉
(1, 1, '太棒了，学到了！', NULL, NOW() - INTERVAL 14 DAY),
(1, 3, '收藏了，回头试试', NULL, NOW() - INTERVAL 14 DAY),
(1, 4, '跟着做了，真的好吃', NULL, NOW() - INTERVAL 13 DAY),

-- 帖子2: 奶茶
(2, 1, '这也太好看了吧', NULL, NOW() - INTERVAL 13 DAY),
(2, 4, '收藏了，回头试试', NULL, NOW() - INTERVAL 13 DAY),
(2, 5, '太棒了，学到了！', NULL, NOW() - INTERVAL 12 DAY),

-- 帖子3: 早午餐
(3, 1, '感谢分享，很实用', NULL, NOW() - INTERVAL 12 DAY),
(3, 4, '这就是我想要的生活', NULL, NOW() - INTERVAL 12 DAY),
(3, 5, '太有用了，已收藏', NULL, NOW() - INTERVAL 11 DAY),

-- 帖子4: 水晶饺
(4, 2, '做得真好，手好巧', NULL, NOW() - INTERVAL 11 DAY),
(4, 3, '请问这个在哪里买的？', NULL, NOW() - INTERVAL 11 DAY),
(4, 5, '太棒了，学到了！', NULL, NOW() - INTERVAL 10 DAY),

-- 帖子5: 秋冬穿搭
(5, 1, '这也太好看了吧', NULL, NOW() - INTERVAL 10 DAY),
(5, 2, '太棒了，学到了！', NULL, NOW() - INTERVAL 10 DAY),
(5, 3, '收藏了，回头试试', NULL, NOW() - INTERVAL 9 DAY),

-- 帖子6: 小个子穿搭
(6, 1, '太有用了，已收藏', NULL, NOW() - INTERVAL 9 DAY),
(6, 2, '感谢分享，很实用', NULL, NOW() - INTERVAL 9 DAY),
(6, 5, '这就是我想要的生活', NULL, NOW() - INTERVAL 8 DAY),

-- 帖子7: 大衣
(7, 1, '这也太好看了吧', NULL, NOW() - INTERVAL 8 DAY),
(7, 2, '想问这件在哪里买的？', NULL, NOW() - INTERVAL 8 DAY),
(7, 3, '太棒了，学到了！', NULL, NOW() - INTERVAL 7 DAY),

-- 帖子8: 大理攻略
(8, 1, '好想去啊！', NULL, NOW() - INTERVAL 7 DAY),
(8, 4, '请问花费大概多少？', NULL, NOW() - INTERVAL 7 DAY),
(8, 5, '照片拍得好美', NULL, NOW() - INTERVAL 6 DAY),

-- 帖子9: 洱海日落
(9, 1, '好想去啊！', NULL, NOW() - INTERVAL 6 DAY),
(9, 2, '照片拍得好美', NULL, NOW() - INTERVAL 6 DAY),
(9, 4, '这就是我想要的生活', NULL, NOW() - INTERVAL 5 DAY),

-- 帖子10: 背包旅行
(10, 1, '太有用了，已收藏', NULL, NOW() - INTERVAL 5 DAY),
(10, 2, '好想去啊！', NULL, NOW() - INTERVAL 5 DAY),
(10, 4, '博主好厉害', NULL, NOW() - INTERVAL 4 DAY),

-- 帖子11: 健身入门
(11, 1, '太有用了，已收藏', NULL, NOW() - INTERVAL 4 DAY),
(11, 2, '感谢分享，很实用', NULL, NOW() - INTERVAL 4 DAY),
(11, 3, '下次也试试这个', NULL, NOW() - INTERVAL 3 DAY),

-- 帖子12: 腹肌挑战
(12, 1, '太棒了，学到了！', NULL, NOW() - INTERVAL 3 DAY),
(12, 2, '加油，你可以的', NULL, NOW() - INTERVAL 3 DAY),
(12, 4, '太有用了，已收藏', NULL, NOW() - INTERVAL 2 DAY),

-- 帖子13: 健身餐
(13, 1, '感谢分享，很实用', NULL, NOW() - INTERVAL 2 DAY),
(13, 2, '收藏了，回头试试', NULL, NOW() - INTERVAL 2 DAY),
(13, 3, '太棒了，学到了！', NULL, NOW() - INTERVAL 1 DAY),

-- 帖子14: 房间改造
(14, 2, '做得真好，手好巧', NULL, NOW() - INTERVAL 1 DAY),
(14, 3, '这也太好看了吧', NULL, NOW() - INTERVAL 1 DAY),
(14, 5, '这就是我想要的生活', NULL, NOW() - 12 HOUR),

-- 帖子15: 周末vlog
(15, 2, '很有生活气息', NULL, NOW() - 12 HOUR),
(15, 3, '照片拍得好美', NULL, NOW() - 6 HOUR),
(15, 4, '太有用了，已收藏', NULL, NOW() - 3 HOUR);

-- 部分评论的回复（约 30% 概率，取前 13 条评论加回复）
INSERT INTO comments (post_id, user_id, content, parent_id, created_at) VALUES
(1, 2, '谢谢夸奖～', 1, NOW() - INTERVAL 13 DAY),
(1, 5, '同问+1', 2, NOW() - INTERVAL 13 DAY),
(2, 2, '在淘宝买的哦', 4, NOW() - INTERVAL 12 DAY),
(3, 2, '谢谢夸奖～', 7, NOW() - INTERVAL 11 DAY),
(4, 1, '在菜市场买的材料', 10, NOW() - INTERVAL 10 DAY),
(5, 4, '都是优衣库的基础款', 13, NOW() - INTERVAL 9 DAY),
(6, 4, '谢谢夸奖～', 16, NOW() - INTERVAL 8 DAY),
(7, 4, '在XX商场买的', 19, NOW() - INTERVAL 7 DAY),
(8, 3, '大概花了3000左右', 22, NOW() - INTERVAL 6 DAY),
(9, 3, '欢迎来玩～', 25, NOW() - INTERVAL 5 DAY),
(10, 3, '加油，你也可以的', 28, NOW() - INTERVAL 4 DAY),
(11, 5, '加油，你也可以的', 31, NOW() - INTERVAL 3 DAY),
(12, 5, '坚持就是胜利', 34, NOW() - INTERVAL 2 DAY);

-- ============================================================
-- 4. 点赞数据（每个帖子 2-3 个赞）
-- ============================================================
INSERT INTO likes (user_id, post_id, created_at) VALUES
-- 帖子1（作者=2）: 用户1,3,4 点赞
(1, 1, NOW() - INTERVAL 14 DAY),
(3, 1, NOW() - INTERVAL 14 DAY),
(4, 1, NOW() - INTERVAL 13 DAY),

-- 帖子2（作者=2）: 用户1,4,5
(1, 2, NOW() - INTERVAL 13 DAY),
(4, 2, NOW() - INTERVAL 13 DAY),
(5, 2, NOW() - INTERVAL 12 DAY),

-- 帖子3（作者=2）: 用户1,4
(1, 3, NOW() - INTERVAL 12 DAY),
(4, 3, NOW() - INTERVAL 12 DAY),

-- 帖子4（作者=1）: 用户2,3,5
(2, 4, NOW() - INTERVAL 11 DAY),
(3, 4, NOW() - INTERVAL 11 DAY),
(5, 4, NOW() - INTERVAL 10 DAY),

-- 帖子5（作者=4）: 用户1,2,3
(1, 5, NOW() - INTERVAL 10 DAY),
(2, 5, NOW() - INTERVAL 10 DAY),
(3, 5, NOW() - INTERVAL 9 DAY),

-- 帖子6（作者=4）: 用户1,2,5
(1, 6, NOW() - INTERVAL 9 DAY),
(2, 6, NOW() - INTERVAL 9 DAY),
(5, 6, NOW() - INTERVAL 8 DAY),

-- 帖子7（作者=4）: 用户1,3
(1, 7, NOW() - INTERVAL 8 DAY),
(3, 7, NOW() - INTERVAL 8 DAY),

-- 帖子8（作者=3）: 用户1,4,5
(1, 8, NOW() - INTERVAL 7 DAY),
(4, 8, NOW() - INTERVAL 7 DAY),
(5, 8, NOW() - INTERVAL 6 DAY),

-- 帖子9（作者=3）: 用户1,2,4
(1, 9, NOW() - INTERVAL 6 DAY),
(2, 9, NOW() - INTERVAL 6 DAY),
(4, 9, NOW() - INTERVAL 5 DAY),

-- 帖子10（作者=3）: 用户1,2,4
(1, 10, NOW() - INTERVAL 5 DAY),
(2, 10, NOW() - INTERVAL 5 DAY),
(4, 10, NOW() - INTERVAL 4 DAY),

-- 帖子11（作者=5）: 用户1,2,3
(1, 11, NOW() - INTERVAL 4 DAY),
(2, 11, NOW() - INTERVAL 4 DAY),
(3, 11, NOW() - INTERVAL 3 DAY),

-- 帖子12（作者=5）: 用户1,2,4
(1, 12, NOW() - INTERVAL 3 DAY),
(2, 12, NOW() - INTERVAL 3 DAY),
(4, 12, NOW() - INTERVAL 2 DAY),

-- 帖子13（作者=5）: 用户1,3
(1, 13, NOW() - INTERVAL 2 DAY),
(3, 13, NOW() - INTERVAL 2 DAY),

-- 帖子14（作者=1）: 用户2,3,5
(2, 14, NOW() - INTERVAL 1 DAY),
(3, 14, NOW() - INTERVAL 1 DAY),
(5, 14, NOW() - 12 HOUR),

-- 帖子15（作者=1）: 用户2,4
(2, 15, NOW() - 12 HOUR),
(4, 15, NOW() - 6 HOUR);

-- ============================================================
-- 5. 收藏数据（每个帖子 1-2 个收藏）
-- ============================================================
INSERT INTO collections (user_id, post_id, created_at) VALUES
-- 帖子1（作者=2）: 用户1,4
(1, 1, NOW() - INTERVAL 14 DAY),
(4, 1, NOW() - INTERVAL 13 DAY),

-- 帖子2（作者=2）: 用户5
(5, 2, NOW() - INTERVAL 12 DAY),

-- 帖子3（作者=2）: 用户1
(1, 3, NOW() - INTERVAL 12 DAY),

-- 帖子4（作者=1）: 用户2,5
(2, 4, NOW() - INTERVAL 11 DAY),
(5, 4, NOW() - INTERVAL 10 DAY),

-- 帖子5（作者=4）: 用户1,2
(1, 5, NOW() - INTERVAL 10 DAY),
(2, 5, NOW() - INTERVAL 9 DAY),

-- 帖子6（作者=4）: 用户5
(5, 6, NOW() - INTERVAL 8 DAY),

-- 帖子7（作者=4）: 用户1
(1, 7, NOW() - INTERVAL 8 DAY),

-- 帖子8（作者=3）: 用户4,5
(4, 8, NOW() - INTERVAL 7 DAY),
(5, 8, NOW() - INTERVAL 6 DAY),

-- 帖子9（作者=3）: 用户1
(1, 9, NOW() - INTERVAL 6 DAY),

-- 帖子10（作者=3）: 用户4
(4, 10, NOW() - INTERVAL 5 DAY),

-- 帖子11（作者=5）: 用户1,3
(1, 11, NOW() - INTERVAL 4 DAY),
(3, 11, NOW() - INTERVAL 3 DAY),

-- 帖子12（作者=5）: 用户2
(2, 12, NOW() - INTERVAL 3 DAY),

-- 帖子13（作者=5）: 用户1
(1, 13, NOW() - INTERVAL 2 DAY),

-- 帖子14（作者=1）: 用户3,5
(3, 14, NOW() - INTERVAL 1 DAY),
(5, 14, NOW() - 12 HOUR),

-- 帖子15（作者=1）: 用户4
(4, 15, NOW() - 6 HOUR);

-- ============================================================
-- 6. 关注数据（每个用户关注 2-3 个其他用户）
-- ============================================================
INSERT INTO follows (follower_id, following_id, created_at) VALUES
-- 用户1 关注 2,3,4（关注了美食家、旅行者、时尚博主）
(1, 2, NOW() - INTERVAL 15 DAY),
(1, 3, NOW() - INTERVAL 14 DAY),
(1, 4, NOW() - INTERVAL 13 DAY),

-- 用户2 关注 1,3（关注了小红、旅行者）
(2, 1, NOW() - INTERVAL 14 DAY),
(2, 3, NOW() - INTERVAL 13 DAY),

-- 用户3 关注 1,2,5（关注了小红、美食家、健身教练）
(3, 1, NOW() - INTERVAL 12 DAY),
(3, 2, NOW() - INTERVAL 11 DAY),
(3, 5, NOW() - INTERVAL 10 DAY),

-- 用户4 关注 1,2,3（关注了小红、美食家、旅行者）
(4, 1, NOW() - INTERVAL 9 DAY),
(4, 2, NOW() - INTERVAL 8 DAY),
(4, 3, NOW() - INTERVAL 7 DAY),

-- 用户5 关注 1,2,4（关注了小红、美食家、时尚博主）
(5, 1, NOW() - INTERVAL 6 DAY),
(5, 2, NOW() - INTERVAL 5 DAY),
(5, 4, NOW() - INTERVAL 4 DAY);
