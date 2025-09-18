import org.example.dao.TagDAO;
import org.example.model.Tag;
import org.example.service.TagManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TagManagerTest {
    private TagDAO tagDAO;
    private TagManager tagManager;

    private Tag defaultTag;
    private Tag validTag;
    private Tag invalidTag;
    @BeforeEach
    public void setUp() {
        tagDAO = mock(TagDAO.class);
        tagManager = new TagManager(tagDAO);

        defaultTag = new Tag("Default");
        defaultTag.setId(1);
        validTag = new Tag("Test tag");
        validTag.setId(2);

        invalidTag = new Tag(null);
        invalidTag.setId(null);
    }

    @Test
    void saveTag_success() {
        tagManager.save(validTag);

        verify(tagDAO,times(1)).save(validTag);
    }

    @Test
    void saveTag_failed_validationError(){
        tagManager.save(invalidTag);

        verify(tagDAO, never()).save(invalidTag);
    }

    @Test
    void findAllTags_success() {
        List<Tag> tags = Collections.singletonList(validTag);

        when(tagDAO.findAll()).thenReturn(tags);
        List<Tag> result = tagManager.findAll();

        assertEquals(tags, result);
        verify(tagDAO,times(1)).findAll();
    }

    @Test
    void findAllTags_failed_DBError(){
        when(tagDAO.findAll()).thenThrow(new RuntimeException("Failed to fetch tags"));

        assertThrows(RuntimeException.class, ()->tagManager.findAll());
        verify(tagDAO).findAll();
    }

    @Test
    void deleteTagById_success() {
        tagManager.deleteById(validTag.getId());

        verify(tagDAO,times(1)).deleteById(validTag.getId());
    }

    @Test
    void deleteTagById_failed_defaultTag(){
        assertThrows(RuntimeException.class, ()->tagManager.deleteById(defaultTag.getId()));
        verify(tagDAO,never()).deleteById(defaultTag.getId());
    }

    @Test
    void deleteTagById_failed_idNull(){
        assertThrows(RuntimeException.class, ()->tagManager.deleteById(null));
        verify(tagDAO,never()).deleteById(anyInt());
    }




}
