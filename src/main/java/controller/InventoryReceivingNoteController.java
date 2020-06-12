package controller;

import model.InventoryReceivingNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.InventoryReceivingNoteService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/inventoryReceivingNotes")
public class InventoryReceivingNoteController {
    @Autowired
    private InventoryReceivingNoteService inventoryReceivingNoteService;

    @RequestMapping(path = "/read/all", method = RequestMethod.GET)
    public List<InventoryReceivingNote> getAllNotes(@RequestParam int page){
        return paginatedDisplay(inventoryReceivingNoteService.getAllNotes(), page);
    }

    @RequestMapping(path = "/read/id", method = RequestMethod.GET)
    public InventoryReceivingNote getNoteById(@RequestParam int id){
        return inventoryReceivingNoteService.getNoteById(id);
    }

    @RequestMapping(path = "/read/date", method = RequestMethod.GET)
    public List<InventoryReceivingNote> getNotesByDate(@RequestParam Date date, @RequestParam int page){
        return paginatedDisplay(inventoryReceivingNoteService.getNotesByDate(date), page);
    }

    @RequestMapping(path = "/read/period", method = RequestMethod.GET)
    public List<InventoryReceivingNote> getNotesByPeriod(@RequestParam Date start, @RequestParam Date end, @RequestParam int page){
        return paginatedDisplay(inventoryReceivingNoteService.getNotesByPeriod(start, end), page);
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public String newNote(@RequestBody InventoryReceivingNote note){
        return inventoryReceivingNoteService.newNote(note);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updateNote(@RequestBody InventoryReceivingNote note){
        return inventoryReceivingNoteService.updateNote(note);
    }

    @RequestMapping(path = "/delete/id", method = RequestMethod.GET)
    public String deleteNote(@RequestParam int id){
        return inventoryReceivingNoteService.deleteNote(id);
    }

    public List<InventoryReceivingNote> paginatedDisplay(List<InventoryReceivingNote> notes, int page) {
        int[] indices = SupportController.getIndices(notes.size(), page);
        return notes.subList(indices[0], indices[1]);
    }
}
