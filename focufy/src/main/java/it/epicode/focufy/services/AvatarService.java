package it.epicode.focufy.services;
import it.epicode.focufy.entities.*;
import it.epicode.focufy.entities.enums.ChronotypeType;
import it.epicode.focufy.entities.enums.QuestionType;
import it.epicode.focufy.entities.enums.TemperType;
import it.epicode.focufy.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvatarService {

    @Autowired
    private AnswerRepo answerRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AvatarRepo avatarRepo;

    @Autowired
    private ChronotypeRepo chronotypeRepo;

    @Autowired
    private TemperRepo temperRepo;

    public Page<Avatar> getAllAvatars(int page, int size, String sortBy){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return avatarRepo.findAll(pageable);
    }

    public Optional<Avatar> getAvatarById(int id){
        return avatarRepo.findById(id);
    }

    public void assignAvatarToUser(int userId){
        User user = userRepo.findById(userId)
                .orElseThrow( ()-> new RuntimeException("User not found"));

        List<Answer> answers = answerRepo.findByUserId(userId);

        ChronotypeType chronotypeType = determineChronotype(answers);
        TemperType temperType = determineTemper(answers);

        Chronotype chronotypeToAssign = chronotypeRepo.findByChronotypeType(chronotypeType)
                .orElseThrow(() -> new RuntimeException("Chronotype not found"));

        Temper temperToAssign = temperRepo.findByTemperType(temperType)
                .orElseThrow(() -> new RuntimeException("Temper not found"));

        Avatar avatar = new Avatar();
        avatar.setChronotype(chronotypeToAssign);
        avatar.setTemper(temperToAssign);
        avatar.setImage(generateAvatarImage(chronotypeType, temperType));
        avatar.setDescription(generateAvatarDescription(chronotypeType, temperType));
        avatar.setUser(user);
        avatarRepo.save(avatar);

        user.setAvatar(avatar);
        userRepo.save(user);

    }

    private ChronotypeType determineChronotype(List<Answer> answers){
        Map<ChronotypeType, Integer> chronotypeCounts = answers.stream()
                .filter(answer -> answer.getQuestion().getQuestionType() == QuestionType.CHRONOTYPE)
                .collect(Collectors.groupingBy(answer -> ChronotypeType.valueOf(answer.getAnswerType().name()), Collectors.summingInt(e -> 1)));

        return chronotypeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(()-> new RuntimeException("No chronotype found"))
                .getKey();
    }

    private TemperType determineTemper(List<Answer> answers) {
        Map<TemperType, Integer> temperCounts = answers.stream()
                .filter(answer -> answer.getQuestion().getQuestionType() == QuestionType.TEMPER)
                .collect(Collectors.groupingBy(answer -> TemperType.valueOf(answer.getAnswerType().name()), Collectors.summingInt(e -> 1)));

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
}
