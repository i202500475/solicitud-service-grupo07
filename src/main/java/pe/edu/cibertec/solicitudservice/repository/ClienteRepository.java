package pe.edu.cibertec.solicitudservice.repository;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import pe.edu.cibertec.solicitudservice.model.Cliente;

@Repository
public class ClienteRepository {

    private final Map<Long, Cliente> clientes = Map.of(
            2001L, new Cliente(2001L, "Carlos Ramirez", "70123451"),
            2002L, new Cliente(2002L, "Maria Torres", "70234562"),
            2003L, new Cliente(2003L, "Luis Mendoza", "70345673"),
            2004L, new Cliente(2004L, "Ana Flores", "70456784")
    );

    public Optional<Cliente> findById(Long clienteId) {
        return Optional.ofNullable(clientes.get(clienteId));
    }
}
