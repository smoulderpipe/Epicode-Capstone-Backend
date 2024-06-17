package it.epicode.focufy.services;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.entities.enums.ChronotypeType;
import it.epicode.focufy.entities.enums.QuestionType;
import it.epicode.focufy.entities.enums.TemperType;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        List<SharedAnswer> sharedAnswers = user.getSharedAnswers(); // Utilizza la relazione diretta

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
        return "image/path/" + chronotype.name().toLowerCase() + "_" + temper.name().toLowerCase() + ".png";
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

}