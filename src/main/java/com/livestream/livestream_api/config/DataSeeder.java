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

    private final UserRepository userRepository;
    private final GiftRepository giftRepository;
    private final LiveStreamRepository streamRepository;
    private final PasswordEncoder passwordEncoder;
    private final StreamConcurrencyManager concurrency;

    @Override
    @Transactional
    public void run(String... args) {

        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping.");
            initializeCounts();
            return;
        }

        User admin = User.builder()
                .username("admin")
                .email("admin@livestream.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .userStatus(User.UserStatus.ACTIVE)
                .emailVerified(true)
                .coinBalance(99999)
                .build();

        User dawood = User.builder().username("Dawood").email("dawood@livestream.com")
                .password(passwordEncoder.encode("Dawood123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(500).build();

        User hamza = User.builder().username("Hamza").email("hamza@livestream.com")
                .password(passwordEncoder.encode("Hamza123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(300).build();

        User shahid = User.builder().username("Shahid").email("shahid@livestream.com")
                .password(passwordEncoder.encode("Shahid123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(750).build();

        User ali = User.builder().username("AliTheCoder").email("ali@livestream.com")
                .password(passwordEncoder.encode("Ali123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(1200).build();

        User omar = User.builder().username("OmarStreams").email("omar@livestream.com")
                .password(passwordEncoder.encode("Omar123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(900).build();

        User yusuf = User.builder().username("YusufLive").email("yusuf@livestream.com")
                .password(passwordEncoder.encode("Yusuf123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(600).build();

        User ibrahim = User.builder().username("IbrahimGamer").email("ibrahim@livestream.com")
                .password(passwordEncoder.encode("Ibrahim123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(1100).build();

        User ahmed = User.builder().username("AhmedPlays").email("ahmed@livestream.com")
                .password(passwordEncoder.encode("Ahmed123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(400).build();

        User khalid = User.builder().username("KhalidX").email("khalid@livestream.com")
                .password(passwordEncoder.encode("Khalid123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(850).build();

        User saeed = User.builder().username("SaeedLive").email("saeed@livestream.com")
                .password(passwordEncoder.encode("Saeed123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(700).build();

        User bilal = User.builder().username("BilalBeats").email("bilal@livestream.com")
                .password(passwordEncoder.encode("Bilal123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(950).build();

        User mustafa = User.builder().username("MustafaVibes").email("mustafa@livestream.com")
                .password(passwordEncoder.encode("Mustafa123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(500).build();

        User tariq = User.builder().username("TariqStreams").email("tariq@livestream.com")
                .password(passwordEncoder.encode("Tariq123")).role(User.Role.USER)
                .userStatus(User.UserStatus.ACTIVE).emailVerified(true).coinBalance(650).build();

        userRepository.saveAll(List.of(
                admin, dawood, hamza, shahid,
                ali, omar, yusuf, ibrahim,
                ahmed, khalid, saeed, bilal,
                mustafa, tariq
        ));

        giftRepository.saveAll(List.of(
                Gift.builder().giftName("Rose").coinValue(10).iconUrl("/icons/rose.png").active(true).build(),
                Gift.builder().giftName("Heart").coinValue(50).iconUrl("/icons/heart.png").active(true).build(),
                Gift.builder().giftName("Crown").coinValue(200).iconUrl("/icons/crown.png").active(true).build(),
                Gift.builder().giftName("Rocket").coinValue(500).iconUrl("/icons/rocket.png").active(true).build(),
                Gift.builder().giftName("Diamond").coinValue(1000).iconUrl("/icons/diamond.png").active(true).build()
        ));

        streamRepository.saveAll(List.of(
                LiveStream.builder()
                        .title("Dawood Java Session")
                        .description("Join me for some coding lessons!")
                        .host(dawood)
                        .thumbnailUrl("/uploads/coding.jpg")
                        .status(LiveStream.StreamStatus.LIVE)
                        .viewerCount(1000000)
                        .likeCount(52258)
                        .build(),

                LiveStream.builder()
                        .title("Dawood Cooks Live!")
                        .description("Cooking TV.")
                        .host(hamza)
                        .thumbnailUrl("/uploads/cooking.jpg")
                        .status(LiveStream.StreamStatus.LIVE)
                        .viewerCount(10000)
                        .likeCount(25528)
                        .build(),

                LiveStream.builder()
                        .title("Dawood Padel Live!")
                        .description("Game TV.")
                        .host(yusuf)
                        .thumbnailUrl("/uploads/padel.jpg")
                        .status(LiveStream.StreamStatus.LIVE)
                        .viewerCount(10)
                        .likeCount(8)
                        .build(),

                LiveStream.builder()
                        .title("BBC Live")
                        .description("Get latest news.")
                        .host(hamza)
                        .thumbnailUrl("/uploads/news.jpg")
                        .status(LiveStream.StreamStatus.SCHEDULED)
                        .viewerCount(0)
                        .likeCount(0)
                        .build(),

                LiveStream.builder()
                        .title("Trump Update")
                        .description("Get latest Trump tweets.")
                        .host(hamza)
                        .thumbnailUrl("/uploads/trump.jpg")
                        .status(LiveStream.StreamStatus.SCHEDULED)
                        .viewerCount(0)
                        .likeCount(0)
                        .build(),

                LiveStream.builder()
                        .title("KSI vs Logan Paul")
                        .description("Bet")
                        .host(hamza)
                        .thumbnailUrl("/uploads/boxing.jpg")
                        .status(LiveStream.StreamStatus.SCHEDULED)
                        .viewerCount(0)
                        .likeCount(0)
                        .build(),

                LiveStream.builder()
                        .title("Stock Market News")
                        .description("MONEYYYYY")
                        .host(hamza)
                        .thumbnailUrl("/uploads/stocks.jpg")
                        .status(LiveStream.StreamStatus.SCHEDULED)
                        .viewerCount(0)
                        .likeCount(0)
                        .build(),

                LiveStream.builder()
                        .title("Taylor Swift Live!")
                        .description("Auto Tune.")
                        .host(ahmed)
                        .thumbnailUrl("/uploads/taylor.jpg")
                        .status(LiveStream.StreamStatus.LIVE)
                        .viewerCount(40000)
                        .likeCount(8527)
                        .build(),

                LiveStream.builder()
                        .title("Atif Aslam Live!")
                        .description("AAAAA.")
                        .host(hamza)
                        .thumbnailUrl("/uploads/atif.jpg")
                        .status(LiveStream.StreamStatus.LIVE)
                        .viewerCount(2300)
                        .likeCount(1478)
                        .build(),

                LiveStream.builder()
                        .title("Tom and Jerry")
                        .description("Cartoon.")
                        .host(hamza)
                        .thumbnailUrl("/uploads/cartoon.jpg")
                        .status(LiveStream.StreamStatus.ENDED)
                        .viewerCount(9000)
                        .likeCount(6347)
                        .build()
        ));

        log.info("Database seeded successfully.");
        initializeCounts();
    }

    private void initializeCounts() {
        streamRepository.findAll().forEach(stream -> {
            concurrency.initLikeCount(stream.getStreamId(), stream.getLikeCount());
            concurrency.resetViewerCount(stream.getStreamId());
        });
    }
}