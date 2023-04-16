package CSIT3214.GroupProject.Service;

import CSIT3214.GroupProject.DataAccessLayer.SuburbRepository;
import CSIT3214.GroupProject.Model.Suburb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class SuburbService {
    @Autowired
    private SuburbRepository suburbRepository;
    private static final Logger logger = LoggerFactory.getLogger(SuburbService.class);

    public Suburb findOrCreateSuburb(String name, String state, double latitude, double longitude) {
        Optional<Suburb> existingSuburb = suburbRepository.findByNameAndState(name, state);

        if (existingSuburb.isPresent()) {
            if (existingSuburb.get().getLatitude() == 0.0 || existingSuburb.get().getLongitude() == 0.0) {
                return updateSuburb(existingSuburb.get(), latitude, longitude);
            }
            return existingSuburb.get();
        } else {
            Suburb newSuburb = new Suburb();
            newSuburb.setName(name);
            newSuburb.setState(state);
            newSuburb.setLatitude(latitude);
            newSuburb.setLongitude(longitude);
            return suburbRepository.save(newSuburb);
        }
    }

    private Suburb updateSuburb(Suburb suburb, double latitude, double longitude) {
        suburb.setLatitude(latitude);
        suburb.setLongitude(longitude);
        return suburbRepository.save(suburb);
    }

    public Suburb findSuburbByNameAndState(String name, String state) {
        Optional<Suburb> existingSuburb = suburbRepository.findByNameAndState(name, state);
        return existingSuburb.orElse(null);
    }
}
