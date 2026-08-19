package com.example.CRUD.service;

import com.example.CRUD.dto.EmployeeRequestDTO;
import com.example.CRUD.dto.EmployeeResponseDTO;
import com.example.CRUD.entity.Employee;
import com.example.CRUD.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import com.example.CRUD.exception.EmployeeNotFoundException;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {
        Employee employee = new Employee();

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());

        Employee savedEmployee = employeeRepository.save(employee);

        return convertToResponseDTO(savedEmployee);
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        return convertToResponseDTO(employee);
    }

    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());

        Employee updatedEmployee = employeeRepository.save(employee);

        return convertToResponseDTO(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }

        employeeRepository.deleteById(id);
    }

    private EmployeeResponseDTO convertToResponseDTO(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment()
        );
    }
}