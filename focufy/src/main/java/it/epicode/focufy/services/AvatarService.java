package it.epicode.focufy.services;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.entities.enums.ChronotypeType;
import it.epicode.focufy.entities.enums.QuestionType;
import it.epicode.focufy.entities.enums.TemperType;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.exceptions.UnauthorizedException;
import it.epicode.focufy.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvatarService {

    @Autowired
    private PersonalAnswerRepo personalAnswerRepo;

    @Autowired
    private SharedAnswerRepo sharedAnswerRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AvatarRepo avatarRepo;

    @Autowired
    private ChronotypeRepo chronotypeRepo;

    @Autowired
    private TemperRepo temperRepo;

    public Page<Avatar> getAllAvatars(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return avatarRepo.findAll(pageable);
    }

    public Optional<Avatar> getAvatarById(int id) {
        return avatarRepo.findById(id);
    }

    public void assignAvatarToUser(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<SharedAnswer> sharedAnswers = user.getSharedAnswers();

        ChronotypeType chronotypeType = determineChronotype(sharedAnswers);
        TemperType temperType = determineTemper(sharedAnswers);

        Chronotype chronotypeToAssign = chronotypeRepo.findByChronotypeType(chronotypeType)
                .orElseThrow(() -> new RuntimeException("Chronotype not found"));

        Temper temperToAssign = temperRepo.findByTemperType(temperType)
                .orElseThrow(() -> new RuntimeException("Temper not found"));

        Avatar avatar = avatarRepo.findByChronotypeAndTemper(chronotypeToAssign, temperToAssign)
                .orElseThrow(() -> new RuntimeException("Avatar not found with chronotype " + chronotypeToAssign + " and temper " + temperToAssign));

        if (!avatar.getUsers().contains(user)) {
            avatar.getUsers().add(user);
            avatarRepo.save(avatar);
        }

        if (user.getAvatar() == null || user.getAvatar().getId() != avatar.getId()) {
            user.setAvatar(avatar);
            userRepo.save(user);
        }
    }

    public boolean isAvatarAssigned(int userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        return user.getAvatar() != null;
    }

    private ChronotypeType determineChronotype(List<SharedAnswer> sharedAnswers) {
        Map<ChronotypeType, Long> chronotypeCounts = sharedAnswers.stream()
                .filter(sharedAnswer -> sharedAnswer.getQuestion().getQuestionType() == QuestionType.CHRONOTYPE)
                .collect(Collectors.groupingBy(sharedAnswer -> ChronotypeType.valueOf(sharedAnswer.getSharedAnswerType().name()), Collectors.counting()));

        return chronotypeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new RuntimeException("No chronotype found"))
                .getKey();
    }

    private TemperType determineTemper(List<SharedAnswer> sharedAnswers) {
        Map<TemperType, Long> temperCounts = sharedAnswers.stream()
                .filter(sharedAnswer -> sharedAnswer.getQuestion().getQuestionType() == QuestionType.TEMPER)
                .collect(Collectors.groupingBy(sharedAnswer -> TemperType.valueOf(sharedAnswer.getSharedAnswerType().name()), Collectors.counting()));

        return temperCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new RuntimeException("No temper found"))
                .getKey();
    }

    private String generateAvatarImage(ChronotypeType chronotype, TemperType temper) {
        String imagePath = "";
        switch(chronotype){
            case LION:
                switch(temper){
                    case WHIMSICAL:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342911/whimsical-lion_pvxrie.jpg";
                        break;
                    case TENACIOUS:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342909/tenacious-lion_nevq66.jpg";
                        break;
                    case TACTICAL:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342906/tactical-lion_h2ixvh.jpg";
                        break;
                    case GREEDY:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342903/greedy-lion_gaq8z7.jpg";
                        break;
                }
                break;
            case BEAR:
                switch(temper){
                    case WHIMSICAL:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342910/whimsical-bear_r26140.jpg";
                        break;
                    case TENACIOUS:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342907/tenacious-bear_pdyjlm.jpg";
                        break;
                    case TACTICAL:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342903/tactical-bear_clc5tp.jpg";
                        break;
                    case GREEDY:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342902/greedy-bear_akqyzv.jpg";

                }
                break;
            case DOLPHIN:
                switch (temper){
                    case WHIMSICAL:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342911/whimsical-dolphin_omksbl.jpg";
                        break;
                    case TENACIOUS:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342908/tenacious-dolphin_lx16sp.jpg";
                        break;
                    case TACTICAL:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342905/tactical-dolphin_jmbeng.jpg";
                        break;
                    case GREEDY:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342902/greedy_dolphin_fynk3v.jpg";
                }
                break;
            case WOLF:
                switch(temper){
                    case WHIMSICAL:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342911/whimsical-dolphin_omksbl.jpg";
                        break;
                    case TENACIOUS:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342909/tenacious-wolf_tgykta.jpg";
                        break;
                    case TACTICAL:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342907/tactical-wolf_osi14o.jpg";
                        break;
                    case GREEDY:
                        imagePath = "https://res.cloudinary.com/dwqbgtodp/image/upload/v1719342904/greedy-wolf_lfgoye.jpg";
                }
            break;
        }
        return imagePath;
    }

    private String generateAvatarDescription(ChronotypeType chronotype, TemperType temper) {
        return "Avatar with chronotype " + chronotype.name() + " and temper " + temper.name();
    }

    public void loadAvatarsFromFile(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length != 2) {
                throw new BadRequestException("Invalid line format: " + line);
            }

            ChronotypeType chronotypeType = ChronotypeType.valueOf(parts[0]);
            TemperType temperType = TemperType.valueOf(parts[1]);

            Chronotype chronotype = chronotypeRepo.findByChronotypeType(chronotypeType)
                    .orElseGet(() -> chronotypeRepo.save(new Chronotype(chronotypeType)));

            Temper temper = temperRepo.findByTemperType(temperType)
                    .orElseGet(() -> temperRepo.save(new Temper(temperType)));

            Avatar avatar = new Avatar();
            avatar.setChronotype(chronotype);
            avatar.setTemper(temper);
            avatar.setImage(generateAvatarImage(chronotypeType, temperType));
            avatar.setDescription(generateAvatarDescription(chronotypeType, temperType));

            avatarRepo.save(avatar);
        }
    }

    public void removeAvatarAssignment(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (authenticatedUserId != userId) {
            throw new UnauthorizedException("You are not allowed to remove avatar assignment for another user.");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));

        user.setAvatar(null);
        userRepo.save(user);
    }

}