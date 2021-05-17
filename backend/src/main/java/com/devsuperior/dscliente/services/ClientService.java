package com.devsuperior.dscliente.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscliente.dto.ClientDTO;
import com.devsuperior.dscliente.entities.Client;
import com.devsuperior.dscliente.repositories.ClientRepository;
import com.devsuperior.dscliente.services.exceptions.DatabaseException;
import com.devsuperior.dscliente.services.exceptions.ResourceNotFoundException;

@Service
public class ClientService {
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Transactional(readOnly = true)
	public Page<ClientDTO> findAllPaged(PageRequest pageRequest){
		return clientRepository
				.findAll(pageRequest)
				.map(client -> new ClientDTO(client));
	}
	
	@Transactional(readOnly = true)
	public ClientDTO findById(Long id){
		return new ClientDTO(clientRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Entity not found!")));
	}

	@Transactional
	public ClientDTO insert(ClientDTO dto) {
		Client entity = new Client();
		copyDtoToEntity(dto, entity);
		entity = clientRepository.save(entity);
		return new ClientDTO(entity);
	}

	@Transactional
	public ClientDTO update(Long id, ClientDTO dto) {
		try {			
			Client entity = clientRepository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = clientRepository.save(entity);
			return new ClientDTO(entity);
			
		}catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(String.format("Id not found: %d", id));
		}
	}

	public void delete(Long id) {
		try {
			clientRepository.deleteById(id);
			
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(String.format("Id not found: %d", id));
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}

	private void copyDtoToEntity(ClientDTO dto, Client entity) {
		entity.setName(dto.getName());
		entity.setCpf(dto.getCpf());
		entity.setBirthDate(dto.getBirthDate());
		entity.setChildren(dto.getChildren());
		entity.setIncome(dto.getIncome());
	}

}
