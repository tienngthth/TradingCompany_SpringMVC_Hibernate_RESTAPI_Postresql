package controller;

import model.InventoryDeliveryNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.InventoryDeliveryNoteService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/inventoryDeliveryNotes")
public class InventoryDeliveryNoteController {
    @Autowired
    private InventoryDeliveryNoteService inventoryDeliveryNoteService;

    @RequestMapping(path = "/read/all", method = RequestMethod.GET)
    public List<InventoryDeliveryNote> getAllNotes(@RequestParam int page){
        return paginatedDisplay(inventoryDeliveryNoteService.getAllNotes(),page);
    }

    @RequestMapping(path = "/read/id", method = RequestMethod.GET)
    public InventoryDeliveryNote getNoteById(@RequestParam int id){
        return inventoryDeliveryNoteService.getNoteById(id);
    }

    @RequestMapping(path = "/read/date", method = RequestMethod.GET)
    public List<InventoryDeliveryNote> getNotesByDate(@RequestParam Date date, @RequestParam int page){
        return paginatedDisplay(inventoryDeliveryNoteService.getNotesByDate(date), page);
    }

    @RequestMapping(path = "/read/period", method = RequestMethod.GET)
    public List<InventoryDeliveryNote> getINotesByPeriod(@RequestParam Date start, @RequestParam Date end, @RequestParam int page){
        return paginatedDisplay(inventoryDeliveryNoteService.getINotesByPeriod(start, end), page);
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String newNote(@RequestBody InventoryDeliveryNote note){
        return inventoryDeliveryNoteService.newNote(note);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updateNote(@RequestBody InventoryDeliveryNote note){
        return inventoryDeliveryNoteService.updateNote(note);
    }

    @RequestMapping(path = "/delete/id", method = RequestMethod.GET)
    public String deleteNote(@RequestParam int id){
        return inventoryDeliveryNoteService.deleteNote(id);
    }

    public List<InventoryDeliveryNote> paginatedDisplay(List<InventoryDeliveryNote> notes, int page) {
        if (page == -1) {
            return notes;
        }
        int firstIndex = (page - 1) * 5;
        if (notes.size() < firstIndex || page <= 0) {
            return new ArrayList<>();
        }
        int lastIndex = firstIndex + 5;
        if (notes.size() < lastIndex) {
            lastIndex = notes.size();
        }
        return notes.subList(firstIndex, lastIndex);
    }
}
