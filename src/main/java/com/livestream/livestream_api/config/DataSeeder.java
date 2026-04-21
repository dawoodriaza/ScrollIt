package com.livestream.livestream_api.config;

import com.livestream.livestream_api.model.*;
import com.livestream.livestream_api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository       userRepository;
    private final GiftRepository       giftRepository;
    private final LiveStreamRepository streamRepository;
    private final PasswordEncoder      passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping.");
            return;
        }
        log.info("Seeding database...");

        User admin = User.builder().username("admin").email("admin@livestream.com")
                .password(passwordEncoder.encode("admin123")).role(User.Role.ADMIN)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(99999).build();

        User Dawood = User.builder().username("Dawood").email("Dawood@livestream.com")
                .password(passwordEncoder.encode("Dawood123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(500).build();

        User Hamza = User.builder().username("Hamza").email("Hamza@livestream.com")
                .password(passwordEncoder.encode("Hamza123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(300).build();

        User Shahid = User.builder().username("Shahid").email("Shahid@livestream.com")
                .password(passwordEncoder.encode("Shahid123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(750).build();

        userRepository.saveAll(List.of(admin, Dawood, Hamza, Shahid));
        log.info("Seeded 4 users.");

        giftRepository.saveAll(List.of(
                Gift.builder().giftName("Rose").coinValue(10).iconUrl("/icons/rose.png").active(true).build(),
                Gift.builder().giftName("Heart").coinValue(50).iconUrl("/icons/heart.png").active(true).build(),
                Gift.builder().giftName("Crown").coinValue(200).iconUrl("/icons/crown.png").active(true).build(),
                Gift.builder().giftName("Rocket").coinValue(500).iconUrl("/icons/rocket.png").active(true).build(),
                Gift.builder().giftName("Diamond").coinValue(1000).iconUrl("/icons/diamond.png").active(true).build()
        ));
        log.info("Seeded 5 gifts.");

        streamRepository.saveAll(List.of(
                LiveStream.builder().title("Dawood Java Session")
                        .description("Join me for some coding lessons!").host(Dawood)
                        .status(LiveStream.StreamStatus.LIVE).viewerCount(1000000).likeCount(52258).build(),
                LiveStream.builder().title("Dawood Cooks Live!")
                        .description("Cooking TV.").host(Hamza)
                        .status(LiveStream.StreamStatus.LIVE).viewerCount(10000).likeCount(25528).build(),
                LiveStream.builder().title("Dawood Padel Live!")
                        .description("Game TV.").host(Hamza)
                        .status(LiveStream.StreamStatus.LIVE).viewerCount(10).likeCount(8).build(),
                LiveStream.builder().title("BBC Live")
                        .description("Get latest news.").host(Hamza)
                        .status(LiveStream.StreamStatus.SCHEDULED).viewerCount(0).likeCount(0).build(),
                LiveStream.builder().title("Trump Update")
                        .description("Get latest Trump tweets.").host(Hamza)
                        .status(LiveStream.StreamStatus.SCHEDULED).viewerCount(0).likeCount(0).build(),
                LiveStream.builder().title("KSI vs logan Paul")
                        .description("Bet").host(Hamza)
                        .status(LiveStream.StreamStatus.SCHEDULED).viewerCount(0).likeCount(0).build(),
                LiveStream.builder().title("Stock Market News")
                        .description("MONEYYYYY").host(Hamza)
                        .status(LiveStream.StreamStatus.SCHEDULED).viewerCount(0).likeCount(0).build(),
                LiveStream.builder().title("Tylor Swift Live!")
                        .description("Auto Tune.").host(Hamza)
                        .status(LiveStream.StreamStatus.LIVE).viewerCount(40000).likeCount(8527).build(),
                LiveStream.builder().title("Atif Aslam Live!")
                        .description("AAAAA.").host(Hamza)
                        .status(LiveStream.StreamStatus.LIVE).viewerCount(2300).likeCount(1478).build(),
                LiveStream.builder().title("Tom and Jerry")
                        .description("Cartoon.").host(Hamza)
                        .status(LiveStream.StreamStatus.ENDED).viewerCount(9000).likeCount(6347).build()
        ));
        log.info("Seeded 5 streams.");

        log.info("Database seeding complete!");
        log.info("Admin login email: admin@livestream.com , password: admin123");
        log.info("User login email: Dawood@livestream.com , password: Dawood123");
    }
}