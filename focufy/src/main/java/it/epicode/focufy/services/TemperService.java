package it.epicode.focufy.services;
import it.epicode.focufy.dtos.CreateOrEditTemperRequestBody;
import it.epicode.focufy.entities.Temper;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.TemperRepo;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

public class TemperService {
    @Autowired
    private TemperRepo temperRepo;

    public List<Temper> getAllTempers(){
        return temperRepo.findAll();
    }

    public Optional<Temper> getTemperById(int id){
        return temperRepo.findById(id);
    }

    public String saveTemper(CreateOrEditTemperRequestBody temperRequestBody){
        Temper temperToSave = new Temper();
        temperToSave.setTemperType(temperRequestBody.getTemperType());
        temperToSave.setDescription(temperRequestBody.getDescription());
        temperRepo.save(temperToSave);
        return "Temper with id=" + temperToSave.getId() + " correctly saved";
    }

    public Temper updateTemper(int id, CreateOrEditTemperRequestBody temperRequestBody) {
        Optional<Temper> temperOptional = getTemperById(id);
        if(temperOptional.isPresent()){
            Temper temperToUpdate = temperOptional.get();
            temperToUpdate.setTemperType(temperRequestBody.getTemperType());
            temperToUpdate.setDescription(temperRequestBody.getDescription());
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
