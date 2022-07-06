package model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;

public class JobDeserializer extends StdDeserializer<JobOffer> {

    public JobDeserializer() {
        this(null);
    }

    @Override
    public JobOffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode productNode = p.getCodec().readTree(p);
        JobOffer jo = new JobOffer();
        jo.setTitle(productNode.get("title").textValue());
        try {
            jo.setSalary(productNode.get("employment_types").get(0).get("salary").get("from").asText(null)
                    + " - " +
                    productNode.get("employment_types").get(0).get("salary").get("to").asText(null));
        } catch (Exception e) {
            jo.setSalary(null);
        }
        jo.setTechnologies(productNode.get("skills").findValuesAsText("name"));
        jo.setDescription(productNode.get("id").asText(null));
        try {
            jo.setStartDate(LocalDate.parse(productNode.get("startDate").asText(null)));
        } catch (Exception e) {
            jo.setStartDate(null);
        }
        jo.setMainProgrammingLanguage(productNode.get("marker_icon").asText(null));

        return jo;
    }

    public JobDeserializer(Class<?> vc) {
        super(vc);
    }
}
