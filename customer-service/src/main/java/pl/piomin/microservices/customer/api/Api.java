package pl.piomin.microservices.customer.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.piomin.microservices.customer.intercomm.AccountClient;
import pl.piomin.microservices.customer.model.Account;
import pl.piomin.microservices.customer.model.Customer;
import pl.piomin.microservices.customer.model.CustomerType;

@RestController
public class Api {

	@Autowired
	private AccountClient accountClient;

	protected Logger logger = Logger.getLogger(Api.class.getName());

	private List<Customer> customers;
	
	@Autowired
	Environment environment;

	

	public Api() {
		customers = new ArrayList<>();
		customers.add(new Customer(1, "Adam Kowalski", CustomerType.INDIVIDUAL));
		customers.add(new Customer(2, "Anna Malinowska", CustomerType.INDIVIDUAL));
		customers.add(new Customer(3, "Paweł Michalski", CustomerType.INDIVIDUAL));
		customers.add(new Customer(4, "Karolina Lewandowska", CustomerType.INDIVIDUAL));
	}

	@RequestMapping("/customers/name/{name}")
	public Customer findByPesel(@PathVariable("name") String name) {
		logger.info(String.format("Customer.findByPesel(%s)", name));
		return customers.stream().filter(it -> it.getName().equals(name)).findFirst().get();
	}

	@RequestMapping("/customers")
	public List<Customer> findAll() {
		logger.info("Customer.findAll()");

		List<Account> accounts = accountClient.getAccounts();
		for (Customer cus : customers) {
			List<Account> customer_accounts = accounts.stream().filter(acc -> acc.getCustomerId().equals(cus.getId()))
					.collect(Collectors.toList());
			cus.setAccounts(customer_accounts);
		}
		String port = environment.getProperty("local.server.port");
		System.out.println(port +" test");
		return customers;
	}

	@RequestMapping("/customers/{id}")
	public Customer findById(@PathVariable("id") Integer id) {
		logger.info(String.format("Customer.findById(%s)", id));
		Customer customer = customers.stream().filter(it -> it.getId().intValue() == id.intValue()).findFirst().get();
		/*
		 * List<Account> accounts = accountClient.getAccounts(id);
		 * customer.setAccounts(accounts);
		 */
		return customer;
	}

}