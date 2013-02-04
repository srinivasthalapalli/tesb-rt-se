package org.talend.esb.sam.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.server.ui.CriteriaAdapter;


public class SAMRestServiceImpl implements SAMRestService {

    SAMProvider provider;

    @Context
    protected UriInfo uriInfo;

    public void setProvider(SAMProvider provider) {
        this.provider = provider;
    }

    @Override
    public Response getEvent(String arg0) {
        return Response.ok(provider.getEventDetails(arg0)).build();
    }

    @Override
    public Response getFlow(String flowID) {
        FlowDetails flowDetails = new FlowDetails();
        List<FlowEvent> flowEvents = provider.getFlowDetails(flowID);
        for (FlowEvent flow : flowEvents) {
            try {
                flow.setDetails(new URL(uriInfo.getBaseUri().toString().concat("/event/").concat(String.valueOf(flow.getId()))));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        flowDetails.setEvents(flowEvents);
        return Response.ok(flowDetails).build();
    }

    @Override
    public Response getFlows(Integer offset, Integer limit, List<String> params) {
        CriteriaAdapter adapter = new CriteriaAdapter(offset, limit, convertParams(params));
        FlowCollection flowCollection = new FlowCollection();
        
        flowCollection = provider.getFlows(adapter);
//        for (Flow flow : flowCollection.getFlows()) {
//            try {
//                flow.setDetails(new URL(uriInfo.getBaseUri().toString().concat("/flow/").concat(String.valueOf(flow.getId()))));
//            } catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//        }
        return Response.ok(flowCollection).build();
    }

    private Map<String, String[]> convertParams(List<String> params) {
        Map<String, String[]> paramsMap = new HashMap<String, String[]>();
        for (String param : params) {
            String[] p = param.split(",");
            if (p.length == 2) {
                paramsMap.put(p[0], new String[] { p[1] });
            }
        }
        return paramsMap;
    }

}