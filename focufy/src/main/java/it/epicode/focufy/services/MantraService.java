package it.epicode.focufy.services;

import it.epicode.focufy.dtos.MantraDTO;
import it.epicode.focufy.entities.Avatar;
import it.epicode.focufy.entities.Chronotype;
import it.epicode.focufy.entities.Mantra;
import it.epicode.focufy.entities.Temper;
import it.epicode.focufy.entities.enums.ChronotypeType;
import it.epicode.focufy.entities.enums.MantraType;
import it.epicode.focufy.entities.enums.TemperType;
import it.epicode.focufy.exceptions.BadRequestException;
import it.epicode.focufy.exceptions.NotFoundException;
import it.epicode.focufy.repositories.MantraRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

@Service
public class MantraService {

    @Autowired
    private MantraRepo mantraRepo;

    public Page<Mantra> getAllMantras(int page, int size, String sortBy){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return mantraRepo.findAll(pageable);
    }

    public Optional<Mantra> getMantraById(int id){
        return mantraRepo.findById(id);
    }

    public void loadMantrasFromFile(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";", 2);
            if (parts.length != 2) {
                throw new BadRequestException("Invalid line format: " + line);
            }

            MantraType mantraType = MantraType.valueOf(parts[0].trim());
            String mantraText = parts[1].trim();

            Mantra mantra = new Mantra();
            mantra.setMantraType(mantraType);
            mantra.setText(mantraText);

            mantraRepo.save(mantra);
        }
    }
}
