package org.example.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.example.dao.TagDAO;
import org.example.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TagManager {

    private static final Logger log = LoggerFactory.getLogger(TagManager.class);

    private final TagDAO tagDAO;
    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();


    public TagManager(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    }

    public Tag save(Tag tag) {
        var valid = VALIDATOR.validate(tag);
        if (!valid.isEmpty()) {
            valid.forEach(v->log.warn("Validation error: {}",v.getMessage()));
            return null;
        }
        return tagDAO.save(tag);
    }


    public List<Tag> findAll() {
        return tagDAO.findAll();
    }
    public void deleteById(Integer id) {
        if (id==null){
            throw new IllegalArgumentException("Tag id cannot be null!");
        }
        tagDAO.deleteById(id);
    }
}
