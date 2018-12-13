/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hello;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private TripRepository tripRepository;

	@Before
	public void deleteAllBeforeTests() throws Exception {
		personRepository.deleteAll();
		tripRepository.deleteAll();
	}

	@Test
	public void shouldReturnRepositoryIndexP() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
				jsonPath("$._links.people").exists());
	}
	@Test
	public void shouldReturnRepositoryIndexT() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
				jsonPath("$._links.trip").exists());
	}

	@Test
	public void shouldCreateEntityP() throws Exception {

		mockMvc.perform(post("/people").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
						status().isCreated()).andExpect(
								header().string("Location", containsString("people/")));
	}
	@Test
	public void shouldCreateEntityT() throws Exception {

		mockMvc.perform(post("/trip").content(
				"{\"tripName\": \"Sauron\"}")).andExpect(
				status().isCreated()).andExpect(
				header().string("Location", containsString("trip/")));
	}

	@Test
	public void shouldRetrieveEntityP() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/people").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.firstName").value("Frodo")).andExpect(
						jsonPath("$.lastName").value("Baggins"));
	}
	@Test
	public void shouldRetrieveEntityT() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/trip").content(
				"{\"tripName\": \"Sauron\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.tripName").value("Sauron"));
	}

	@Test
	public void shouldQueryEntityP() throws Exception {

		mockMvc.perform(post("/people").content(
				"{ \"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
						status().isCreated());

		mockMvc.perform(
				get("/people/search/findByLastName?name={name}", "Baggins")).andExpect(
						status().isOk()).andExpect(
								jsonPath("$._embedded.people[0].firstName").value(
										"Frodo"));
	}
	@Test
	public void shouldQueryEntityT() throws Exception {

		mockMvc.perform(post("/trip").content(
				"{ \"tripName\": \"Sauron\"}")).andExpect(
				status().isCreated());

		mockMvc.perform(
				get("/trip/search/findByTripName?tripName={tripName}", "Sauron")).andExpect(
				status().isOk()).andExpect(
				jsonPath("$._embedded.trip[0].tripName").value(
						"Sauron"));
	}

	@Test
	public void shouldUpdateEntityP() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/people").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(put(location).content(
				"{\"firstName\": \"Bilbo\", \"lastName\":\"Baggins\"}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.firstName").value("Bilbo")).andExpect(
						jsonPath("$.lastName").value("Baggins"));
	}
	@Test
	public void shouldUpdateEntityT() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/trip").content(
				"{\"tripName\": \"Sauron\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(put(location).content(
				"{\"tripName\": \"Sauron\"}")).andExpect(
				status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.tripName").value("Sauron"));
	}

	@Test
	public void shouldPartiallyUpdateEntityP() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/people").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(
				patch(location).content("{\"firstName\": \"Bilbo Jr.\"}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.firstName").value("Bilbo Jr.")).andExpect(
						jsonPath("$.lastName").value("Baggins"));
	}
	@Test
	public void shouldPartiallyUpdateEntityT() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/trip").content(
				"{\"tripName\": \"Sauron\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(
				patch(location).content("{\"tripName\": \"Sauron Jr.\"}")).andExpect(
				status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.tripName").value("Sauron Jr."));
	}

	@Test
	public void shouldDeleteEntityP() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/people").content(
				"{ \"firstName\": \"Bilbo\", \"lastName\":\"Baggins\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}
	@Test
	public void shouldDeleteEntityT() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/trip").content(
				"{ \"tripName\": \"Sauron\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}
}