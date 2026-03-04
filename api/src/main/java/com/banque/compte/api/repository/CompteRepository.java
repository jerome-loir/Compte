package com.banque.compte.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.banque.compte.api.model.Compte;

@Repository
public interface CompteRepository extends CrudRepository<Compte,Long>{

}
