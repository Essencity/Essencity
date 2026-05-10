package com.xiaohongshu;

import com.xiaohongshu.entity.*;
import com.xiaohongshu.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final CollectionRepository collectionRepository;
    private final FollowRepository followRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Random random = new Random();

    public DataInitializer(UserRepository userRepository,
                           PostRepository postRepository,
                           CommentRepository commentRepository,
                           LikeRepository likeRepository,
                           CollectionRepository collectionRepository,
                           FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.collectionRepository = collectionRepository;
        this.followRepository = followRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 只在数据库为空时插入测试数据
        if (userRepository.count() > 0) {
            System.out.println("数据库已有数据，跳过测试数据初始化");
            return;
        }

        System.out.println("开始初始化测试数据...");

        // 1. 创建测试用户
        List<User> users = createUsers();

        // 2. 创建测试帖子
        List<Post> posts = createPosts(users);

        // 3. 创建评论
        createComments(users, posts);

        // 4. 创建点赞、收藏、关注
        createInteractions(users, posts);

        System.out.println("测试数据初始化完成！");
        System.out.println("- 用户: " + userRepository.count());
        System.out.println("- 帖子: " + postRepository.count());
        System.out.println("- 评论: " + commentRepository.count());
        System.out.println("- 点赞: " + likeRepository.count());
        System.out.println("- 收藏: " + collectionRepository.count());
        System.out.println("- 关注: " + followRepository.count());
    }

    private List<User> createUsers() {
        String encodedPassword = passwordEncoder.encode("123456");

        User user1 = new User();
        user1.setUsername("xiaohong");
        user1.setPassword(encodedPassword);
        user1.setNickname("小红爱生活");
        user1.setAvatar("/uploads/default-avatar.png");
        user1.setBio("分享生活的美好瞬间 | 美食达人 | 旅行爱好者");
        user1.setGender("女");

        User user2 = new User();
        user2.setUsername("meishi");
        user2.setPassword(encodedPassword);
        user2.setNickname("美食家小王");
        user2.setAvatar("/uploads/default-avatar.png");
        user2.setBio("每天分享一道家常菜 | 厨房是我的快乐天地");
        user2.setGender("男");

        User user3 = new User();
        user3.setUsername("travel");
        user3.setPassword(encodedPassword);
        user3.setNickname("背包客小李");
        user3.setAvatar("/uploads/default-avatar.png");
        user3.setBio("走遍千山万水 | 用镜头记录世界");
        user3.setGender("男");

        User user4 = new User();
        user4.setUsername("fashion");
        user4.setPassword(encodedPassword);
        user4.setNickname("时尚达人Lucy");
        user4.setAvatar("/uploads/default-avatar.png");
        user4.setBio("穿搭是一种态度 | 时尚博主 | 合作私信");
        user4.setGender("女");

        User user5 = new User();
        user5.setUsername("fitness");
        user5.setPassword(encodedPassword);
        user5.setNickname("健身教练阿杰");
        user5.setAvatar("/uploads/default-avatar.png");
        user5.setBio("自律给我自由 | 健身8年 | 科学健身");
        user5.setGender("男");

        List<User> users = Arrays.asList(user1, user2, user3, user4, user5);
        return userRepository.saveAll(users);
    }

    private List<Post> createPosts(List<User> users) {
        List<Post> posts = new java.util.ArrayList<>();

        // 美食类帖子
        posts.add(createPost(users.get(1), "超好吃的红烧肉做法",
            "今天分享一道家常红烧肉，肥而不腻，入口即化！\n\n材料：五花肉500g、冰糖、生抽、老抽、料酒、八角、桂皮\n\n做法：\n1. 五花肉切块焯水\n2. 锅中放油炒糖色\n3. 放入肉块翻炒上色\n4. 加调料和热水炖1小时\n5. 大火收汁即可",
            "image", "/uploads/test/food-1.jpg", "美食"));

        posts.add(createPost(users.get(1), "自制奶茶比外面好喝",
            "再也不用排队买奶茶了！自制珍珠奶茶，简单又好喝～\n\n材料：红茶包、牛奶、黑糖、木薯粉\n\n步骤超简单，跟着视频学起来！",
            "image", "/uploads/test/food-2.jpg", "美食"));

        posts.add(createPost(users.get(1), "周末早午餐brunch",
            "周末睡到自然醒，给自己做一份精致的早午餐\n\n法式吐司+牛油果+煎蛋+水果\n\n生活需要仪式感",
            "image", "/uploads/test/food-3.jpg", "美食"));

        posts.add(createPost(users.get(0), "虾滑时蔬水晶饺",
            "颜值超高又好吃的水晶饺！\n\n馅料：虾滑+胡萝卜+玉米+青豆\n皮：澄面+淀粉\n\n蒸10分钟就好啦，晶莹剔透超好看",
            "image", "/uploads/test/food-4.jpg", "美食"));

        // 穿搭类帖子
        posts.add(createPost(users.get(3), "秋冬穿搭合集",
            "整理了最近的秋冬穿搭，每一套都好爱！\n\n风格：简约通勤风\n色系：大地色系为主\n\n姐妹们觉得哪套最好看？",
            "image", "/uploads/test/fashion-1.jpg", "穿搭"));

        posts.add(createPost(users.get(3), "小个子女生穿搭技巧",
            "155cm的我终于找到了显高的穿搭秘诀！\n\n1. 高腰线是关键\n2. 同色系搭配拉长比例\n3. 尖头鞋显腿长\n4. 适当露肤更显高",
            "image", "/uploads/test/fashion-2.jpg", "穿搭"));

        posts.add(createPost(users.get(3), "这件大衣太好看了",
            "新入的驼色大衣，质感超好！\n\n搭配白色高领毛衣+黑色阔腿裤\n简单又有气质\n\n#穿搭分享 #秋冬穿搭",
            "image", "/uploads/test/fashion-3.jpg", "穿搭"));

        // 旅行类帖子
        posts.add(createPost(users.get(2), "云南大理旅行攻略",
            "终于去了心心念念的大理！\n\n行程安排：\nDay1: 大理古城-人民路-洋人街\nDay2: 洱海环湖骑行\nDay3: 苍山索道-寂照庵\nDay4: 喜洲古镇-海舌公园\n\n详细攻略看图～",
            "image", "/uploads/test/travel-1.jpg", "旅行"));

        posts.add(createPost(users.get(2), "日落时分的洱海",
            "在洱海边看了此生最美的日落\n\n金色的阳光洒在湖面上\n远处的苍山若隐若现\n\n这一刻，觉得所有的奔波都值得",
            "image", "/uploads/test/travel-2.jpg", "旅行"));

        posts.add(createPost(users.get(2), "一个人的背包旅行",
            "第一次一个人背包旅行，去了厦门\n\n在鼓浪屿迷路了三次\n在曾厝垵吃了一整条街\n在环岛路骑了一下午的车\n\n一个人旅行，是和自己的对话",
            "image", "/uploads/test/travel-3.jpg", "旅行"));

        // 健身类帖子
        posts.add(createPost(users.get(4), "新手健身入门指南",
            "很多小伙伴问我新手该怎么开始健身\n\n给大家整理了一份入门计划：\n\n周一：胸+三头\n周二：背+二头\n周三：休息\n周四：肩+核心\n周五：腿\n周末：有氧\n\n每个动作3组，每组12次",
            "image", "/uploads/test/fitness-1.jpg", "健身"));

        posts.add(createPost(users.get(4), "30天腹肌挑战",
            "跟着这个计划练30天，腹肌真的会出来！\n\n每天15分钟：\n- 卷腹 3x20\n- 平板支撑 3x45秒\n- 俄罗斯转体 3x20\n- 登山者 3x20\n\n坚持就是胜利！",
            "image", "/uploads/test/fitness-2.jpg", "健身"));

        posts.add(createPost(users.get(4), "健身餐这样吃",
            "健身三分练七分吃！\n\n分享我的健身餐：\n早餐：燕麦+蛋白+香蕉\n午餐：鸡胸肉+糙米+西兰花\n晚餐：三文鱼+红薯+沙拉\n\n蛋白质摄入量 = 体重(kg) x 1.5g",
            "image", "/uploads/test/fitness-3.jpg", "健身"));

        // 生活类帖子
        posts.add(createPost(users.get(0), "我的房间改造记录",
            "花了一个周末改造了我的小房间\n\n改造前：杂乱无章\n改造后：温馨小窝\n\n花费不到500块，效果超满意！\n\n#房间改造 #租房改造",
            "image", "/uploads/test/life-1.jpg", "家居"));

        posts.add(createPost(users.get(0), "周末vlog",
            "记录一下美好的周末时光\n\n睡到自然醒 → 做早餐 → 逛花市 → 下午茶 → 看电影\n\n平凡的日子也要好好过",
            "video", "/uploads/test/life-2.jpg", "影视"));

        return postRepository.saveAll(posts);
    }

    private Post createPost(User author, String title, String description,
                            String type, String url, String tag) {
        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(title);
        post.setDescription(description);
        post.setType(type);
        post.setUrl(url);
        post.setCoverUrl(type.equals("video") ? url.replace(".mp4", "-cover.jpg") : url);
        post.setTag(tag);
        return post;
    }

    private void createComments(List<User> users, List<Post> posts) {
        List<String> commentTexts = Arrays.asList(
            "太棒了，学到了！",
            "收藏了，回头试试",
            "这也太好看了吧",
            "请问这个在哪里买的？",
            "感谢分享，很实用",
            "做得真好，手好巧",
            "好想去啊！",
            "请问花费大概多少？",
            "跟着做了，真的好吃",
            "太有用了，已收藏",
            "博主好厉害",
            "下次也试试这个",
            "照片拍得好美",
            "很有生活气息",
            "这就是我想要的生活"
        );

        List<String> replyTexts = Arrays.asList(
            "同问+1",
            "谢谢夸奖～",
            "在XX买的哦",
            "大概花了XX元",
            "加油，你也可以的",
            "欢迎来玩～"
        );

        for (Post post : posts) {
            // 每个帖子3-6条评论
            int commentCount = 3 + random.nextInt(4);
            for (int i = 0; i < commentCount; i++) {
                User commenter = users.get(random.nextInt(users.size()));
                // 不让作者评论自己的帖子
                if (commenter.getId().equals(post.getAuthor().getId())) {
                    commenter = users.get((users.indexOf(commenter) + 1) % users.size());
                }

                Comment comment = new Comment();
                comment.setPost(post);
                comment.setUser(commenter);
                comment.setContent(commentTexts.get(random.nextInt(commentTexts.size())));
                comment = commentRepository.save(comment);

                // 30%概率有回复
                if (random.nextDouble() < 0.3) {
                    User replier = users.get(random.nextInt(users.size()));
                    Comment reply = new Comment();
                    reply.setPost(post);
                    reply.setUser(replier);
                    reply.setParentId(comment.getId());
                    reply.setContent(replyTexts.get(random.nextInt(replyTexts.size())));
                    reply.setReplyToUser(commenter);
                    reply.setReplyToCommentId(comment.getId());
                    commentRepository.save(reply);
                }
            }
        }
    }

    private void createInteractions(List<User> users, List<Post> posts) {
        // 创建点赞 - 每个帖子随机被2-4个用户点赞
        for (Post post : posts) {
            int likeCount = 2 + random.nextInt(3);
            List<User> shuffledUsers = new java.util.ArrayList<>(users);
            java.util.Collections.shuffle(shuffledUsers);

            for (int i = 0; i < Math.min(likeCount, users.size()); i++) {
                User user = shuffledUsers.get(i);
                if (!user.getId().equals(post.getAuthor().getId())) {
                    Like like = new Like();
                    like.setUser(user);
                    like.setPost(post);
                    likeRepository.save(like);
                }
            }
        }

        // 创建收藏 - 每个帖子随机被1-3个用户收藏
        for (Post post : posts) {
            int collectionCount = 1 + random.nextInt(3);
            List<User> shuffledUsers = new java.util.ArrayList<>(users);
            java.util.Collections.shuffle(shuffledUsers);

            for (int i = 0; i < Math.min(collectionCount, users.size()); i++) {
                User user = shuffledUsers.get(i);
                if (!user.getId().equals(post.getAuthor().getId())) {
                    Collection collection = new Collection();
                    collection.setUser(user);
                    collection.setPost(post);
                    collectionRepository.save(collection);
                }
            }
        }

        // 创建关注关系 - 每个用户关注2-4个其他用户
        for (User follower : users) {
            int followCount = 2 + random.nextInt(3);
            List<User> shuffledUsers = new java.util.ArrayList<>(users);
            java.util.Collections.shuffle(shuffledUsers);

            for (int i = 0; i < shuffledUsers.size() && followCount > 0; i++) {
                User following = shuffledUsers.get(i);
                if (!follower.getId().equals(following.getId())) {
                    Follow follow = new Follow();
                    follow.setFollower(follower);
                    follow.setFollowing(following);
                    followRepository.save(follow);
                    followCount--;
                }
            }
        }
    }
}
