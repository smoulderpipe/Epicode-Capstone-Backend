package it.epicode.focufy.services;
import it.epicode.focufy.dtos.TemperDTO;
import it.epicode.focufy.entities.Temper;
import it.epicode.focufy.entities.enums.RiskType;
import it.epicode.focufy.entities.enums.StrengthType;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.TemperRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemperService {
    @Autowired
    private TemperRepo temperRepo;

    public List<Temper> getAllTempers(){
        return temperRepo.findAll();
    }

    public Optional<Temper> getTemperById(int id){
        return temperRepo.findById(id);
    }

    public String saveTemper(TemperDTO temperRequestBody){
        Temper temperToSave = new Temper(temperRequestBody.getTemperType());
        temperToSave.setDescription(temperRequestBody.getDescription());
        switch (temperRequestBody.getTemperType()) {
            case WHIMSICAL:
                temperToSave.setStrengthType(StrengthType.ACUMEN);
                temperToSave.setRiskType(RiskType.DISTRACTION);
                break;
            case TENACIOUS:
                temperToSave.setStrengthType(StrengthType.DISCIPLINE);
                temperToSave.setRiskType(RiskType.BOREDOM);
                break;
            case TACTICAL:
                temperToSave.setStrengthType(StrengthType.FORESIGHT);
                temperToSave.setRiskType(RiskType.OVERTHINKING);
                break;
            case GREEDY:
                temperToSave.setStrengthType(StrengthType.PASSION);
                temperToSave.setRiskType(RiskType.BURNOUT);
                break;
            default:
                throw new IllegalArgumentException("Unknown temper type: " + temperRequestBody.getTemperType());
        }
        temperRepo.save(temperToSave);
        return "Temper with id=" + temperToSave.getId() + " correctly saved";
    }

    public Temper updateTemper(int id, TemperDTO temperRequestBody) {
        Optional<Temper> temperOptional = getTemperById(id);
        if(temperOptional.isPresent()){
            Temper temperToUpdate = temperOptional.get();
            temperToUpdate.setTemperType(temperRequestBody.getTemperType());
            temperToUpdate.setDescription(temperRequestBody.getDescription());
            switch (temperRequestBody.getTemperType()) {
                case WHIMSICAL:
                    temperToUpdate.setStrengthType(StrengthType.ACUMEN);
                    temperToUpdate.setRiskType(RiskType.DISTRACTION);
                    break;
                case TENACIOUS:
                    temperToUpdate.setStrengthType(StrengthType.DISCIPLINE);
                    temperToUpdate.setRiskType(RiskType.BOREDOM);
                    break;
                case TACTICAL:
                    temperToUpdate.setStrengthType(StrengthType.FORESIGHT);
                    temperToUpdate.setRiskType(RiskType.OVERTHINKING);
                    break;
                case GREEDY:
                    temperToUpdate.setStrengthType(StrengthType.PASSION);
                    temperToUpdate.setRiskType(RiskType.BURNOUT);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown temper type: " + temperRequestBody.getTemperType());
            }
            return temperRepo.save(temperToUpdate);
        } else {
            throw new NotFoundException("Temper with id=" + id + " not found");
        }
    }

    public String deleteTemper(int id){
        Optional<Temper> temperOptional = getTemperById(id);
        if(temperOptional.isPresent()){
            Temper temperToDelete = temperOptional.get();
            temperRepo.delete(temperToDelete);
            return "Temper with id=" + id + " correctly deleted";
        } else {
            throw new NotFoundException("Temper with id=" + id + " not found");
        }
    }
}
