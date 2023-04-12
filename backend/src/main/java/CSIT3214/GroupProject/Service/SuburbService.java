package CSIT3214.GroupProject.Service;

import CSIT3214.GroupProject.DataAccessLayer.SuburbRepository;
import CSIT3214.GroupProject.Model.Suburb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SuburbService {
    @Autowired
    private SuburbRepository suburbRepository;

    public Suburb findOrCreateSuburb(String name, String state, double latitude, double longitude) {
        Optional<Suburb> existingSuburb = suburbRepository.findByNameAndState(name, state);

        if (existingSuburb.isPresent()) {
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
}
