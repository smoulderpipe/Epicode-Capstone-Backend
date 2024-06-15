package it.epicode.focufy.services;
import it.epicode.focufy.dtos.CreateOrEditChronotypeRequestBody;
import it.epicode.focufy.entities.Chronotype;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.ChronotypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChronotypeService {

    @Autowired
    private ChronotypeRepo chronotypeRepo;

    public List<Chronotype> getAllChronotypes(){
        return chronotypeRepo.findAll();
    }

    public Optional<Chronotype> getChronotypeById(int id){
        return chronotypeRepo.findById(id);
    }

    public String saveChronotype(CreateOrEditChronotypeRequestBody chronotypeRequestBody){
        Chronotype chronotypeToSave = new Chronotype();
        chronotypeToSave.setChronotypeType(chronotypeRequestBody.getChronotypeType());
        chronotypeToSave.setDescription(chronotypeRequestBody.getDescription());
        chronotypeRepo.save(chronotypeToSave);
        return "Chronotype with id=" + chronotypeToSave.getId() + " correctly saved";
    }

    public Chronotype updateChronotype(int id, CreateOrEditChronotypeRequestBody chronotypeRequestBody) {
        Optional<Chronotype> chronotypeOptional = getChronotypeById(id);
        if(chronotypeOptional.isPresent()){
            Chronotype chronotypeToUpdate = chronotypeOptional.get();
            chronotypeToUpdate.setChronotypeType(chronotypeRequestBody.getChronotypeType());
            chronotypeToUpdate.setDescription(chronotypeRequestBody.getDescription());
            return chronotypeRepo.save(chronotypeToUpdate);
        } else {
            throw new NotFoundException("Chronotype with id=" + id + " not found");
        }
    }

    public String deleteChronotype(int id){
        Optional<Chronotype> chronotypeOptional = getChronotypeById(id);
        if(chronotypeOptional.isPresent()){
            Chronotype chronotypeToDelete = chronotypeOptional.get();
            chronotypeRepo.delete(chronotypeToDelete);
            return "Chronotype with id=" + id + " correctly deleted";
        } else {
            throw new NotFoundException("Chronotype with id=" + id + " not found");
        }
    }
}
