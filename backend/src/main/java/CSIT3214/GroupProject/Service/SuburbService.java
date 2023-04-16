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
            Suburb currentSuburb = existingSuburb.get();
            if (currentSuburb.getLatitude() == 0.0 && currentSuburb.getLongitude() == 0.0) {
                // Update the existing suburb with the new latitude and longitude values
                currentSuburb.setLatitude(latitude);
                currentSuburb.setLongitude(longitude);
                return suburbRepository.save(currentSuburb);
            } else {
                return currentSuburb;
            }
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
        if (existingSuburb.isPresent()) {
            Suburb validSuburb = existingSuburb.filter(suburb -> suburb.getLatitude() != 0.0 && suburb.getLongitude() != 0.0).orElse(null);
            if (validSuburb != null) {
                return validSuburb;
            } else {
                Suburb emptyCoordinatesSuburb = existingSuburb.filter(suburb -> suburb.getLatitude() == 0.0 && suburb.getLongitude() == 0.0).orElse(null);
                if (emptyCoordinatesSuburb != null) {
                    return emptyCoordinatesSuburb;
                } else {
                    // Multiple suburbs found, but none with valid coordinates or empty coordinates
                    logger.warn("Multiple suburbs found with name: {} and state: {}", name, state);
                    return null;
                }
            }
        } else {
            return null;
        }
    }
}