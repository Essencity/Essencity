package com.xiaohongshu;

import com.xiaohongshu.entity.Post;
import com.xiaohongshu.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;

    public DataInitializer(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        updatePostTag("虾滑时蔬水晶饺", "美食");
        updatePostTag("在朋友家吃过一次", "美食");
        updatePostTag("这种酱，蘸啥会不好吃", "美食");
        updatePostTag("要这样网恋吗", "穿搭");
    }

    private void updatePostTag(String titleKeyword, String tag) {
        List<Post> posts = postRepository.findByTitleContainingOrderByCreatedAtDesc(titleKeyword);
        for (Post post : posts) {
            post.setTag(tag);
            postRepository.save(post);
            System.out.println("Updated tag for post: " + post.getTitle() + " to " + tag);
        }
    }
}
