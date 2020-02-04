package com.clarion.fundonote.service.serviceimp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarion.fundonote.dto.UserNoteDTO;
import com.clarion.fundonote.model.UserDetails;
import com.clarion.fundonote.model.UserNotes;
import com.clarion.fundonote.repository.NoteRepository;
import com.clarion.fundonote.repository.UserRepository;
import com.clarion.fundonote.service.NoteService;
import com.clarion.fundonote.utils.JwtUtility;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	NoteRepository noteRepository;

	@Autowired
	private ModelMapper modelMapper;

	public UserNotes createNote(UserNoteDTO userNoteDto, String token) {
		Long userId = JwtUtility.validateToken(token);
		UserNotes notes = null;
		if (userId != null) {
			Optional<UserDetails> userDetails = userRepository.findById(userId);
			if (userDetails.isPresent())
				notes = modelMapper.map(userNoteDto, UserNotes.class);
			notes.setCreatedDateTime(LocalDateTime.now());
			notes.setUpdatedDateTime(LocalDateTime.now());
			userDetails.get().getNotes().add(notes);
			notes = noteRepository.save(notes);
			return notes;
		}
		return notes;
	}

	@Override
	public UserNotes deleteNode(Long noteId, String token) {
		Long userId = JwtUtility.validateToken(token);
		Optional<UserDetails> userDetails = userRepository.findById(userId);
		Optional<UserNotes> notes = null;
		if (userDetails.isPresent() && noteId != null)
			notes = noteRepository.findById(noteId);
		noteRepository.deleteById(noteId);
		return notes.get();
	}

	@Override
	public UserNotes updateNotes(UserNotes updateNoteDto, String token) {
		Long userId = JwtUtility.validateToken(token);
		Optional<UserDetails> userDetails = userRepository.findById(userId);
		Optional<UserNotes> note = null;
		if (userDetails.isPresent())
			note = noteRepository.findById(updateNoteDto.getId());
		note.get().setNoteTitle(updateNoteDto.getNoteTitle());
		note.get().setDescription(updateNoteDto.getDescription());
		note.get().setUpdatedDateTime(LocalDateTime.now());
		noteRepository.save(note.get());
		return note.get();
	}

	@Override
	public List<UserNotes> getNotes(String token) {
		Long userId = JwtUtility.validateToken(token);
		Optional<UserDetails> userDetails = userRepository.findById(userId);
		List<UserNotes> notes = null;
		if (userDetails.isPresent())
			notes = userDetails.get().getNotes();
		return notes;
	}

	@Override
	public UserNotes updateColor(Long noteId, String color, String token) {
		System.out.println("color: " + color);
		Long userId = JwtUtility.validateToken(token);
		Optional<UserDetails> userDetails = userRepository.findById(userId);
		UserNotes userNote = null;
		if (userDetails.isPresent()) {
			Optional<UserNotes> notes = noteRepository.findById(noteId);
			userNote = notes.get();
			userNote.setColor(color);
			userNote.setUpdatedDateTime(LocalDateTime.now());
			noteRepository.save(userNote);
		}
		return userNote;
	}
}
