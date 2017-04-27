package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ResourceWarehouse {

	// Channel,<Uri,<Owner,Resource>>
	private HashMap<String, HashMap<String, HashMap<String, Resource>>> resourceMap;

	public ResourceWarehouse() {
		resourceMap = new HashMap<>();
	}

	public boolean AddResource(Resource resource) {
		boolean success = true;
		if (resourceMap.containsKey(resource.getChannel())) {
			if (resourceMap.get(resource.getChannel()).containsKey(resource.getURI())) {
				Set<String> owners = resourceMap.get(resource.getChannel()).get(resource.getURI()).keySet();
				// same channel and uri check owner
				for (String string : owners) {
					if (!string.equals(resource.getOwner())) {
						success = false;
					}
				}
				// true => no different then overwrite
				if (success) {
					resourceMap.get(resource.getChannel()).get(resource.getURI()).replace(resource.getOwner(),
							resource);
				}
			} else {
				// uri different => new one
				HashMap<String, Resource> ownerResourceMap = new HashMap<String, Resource>();
				ownerResourceMap.put(resource.getOwner(), resource);

				resourceMap.get(resource.getChannel()).put(resource.getURI(), ownerResourceMap);
			}
		} else {
			HashMap<String, Resource> ownerResourceMap = new HashMap<String, Resource>();
			ownerResourceMap.put(resource.getOwner(), resource);
			HashMap<String, HashMap<String, Resource>> uriResourceMap = new HashMap<>();
			uriResourceMap.put(resource.getURI(), ownerResourceMap);

			resourceMap.put(resource.getChannel(), uriResourceMap);

		}

		return success;
	}

	public boolean RemoveResource(Resource resource) {
		boolean success = false;

		if (resourceMap.containsKey(resource.getChannel())) {

			if (resourceMap.get(resource.getChannel()).containsKey(resource.getURI())) {

				if (resourceMap.get(resource.getChannel()).get(resource.getURI()).containsKey(resource.getOwner())) {
					// same channel uri and owner
					// remove
					resourceMap.get(resource.getChannel()).get(resource.getURI()).remove(resource.getOwner());
					success = true;

					if (resourceMap.get(resource.getChannel()).get(resource.getURI()).isEmpty()) {
						// check 2nd map is empty
						// if is, remove
						resourceMap.get(resource.getChannel()).remove(resource.getURI());

						if (resourceMap.get(resource.getChannel()).isEmpty()) {
							// check 1st map is empty
							// if is, remove
							resourceMap.remove(resource.getChannel());
						}
					}
				}
			}
		}
		return success;
	}

	public int getSizeOfWarehourse() {
		return resourceMap.size();
	}

	/**
	 * Find the Resource via the three keys
	 * 
	 * @param channel
	 *            Channel of the resource
	 * @param uri
	 *            Uri of the resource
	 * @param Owner
	 *            Owner of the resource
	 * @return Resource based on the parameter given, null if no resource is
	 *         founded
	 */
	private Resource FindResource(String channel, String uri, String Owner) {
		Resource resource = null;
		if (resourceMap.containsKey(channel)) {

			if (resourceMap.get(channel).containsKey(uri)) {

				if (resourceMap.get(channel).get(uri).containsKey(Owner)) {
					// same channel uri and owner
					// remove
					resource = resourceMap.get(channel).get(uri).get(Owner);
				}
			}
		}
		return resource;
	}

	public boolean FindResource(String channel, String uri) throws OperationRunningException {
		boolean existResource = false;
		if (resourceMap.containsKey(channel)) {
			if (resourceMap.get(channel).containsKey(uri)) {
				List<Resource> resList = new ArrayList<Resource>();
				if (!resourceMap.get(channel).get(uri).isEmpty()) {
					existResource = true;
				}
			}
		}
		return existResource;
	}

	public void printResourceMap(){
		String channel = null;
		String uri = null;
		String owner = null;
		
		for(HashMap.Entry<String,HashMap<String,HashMap<String,Resource>>> entry:resourceMap.entrySet()){
			channel=entry.getKey();
			for(HashMap.Entry<String,HashMap<String, Resource>> entry1:entry.getValue().entrySet()){
				uri=entry.getKey();
				for(HashMap.Entry<String,Resource> entry2: entry1.getValue().entrySet()){
					owner=entry2.getKey();
					System.out.println("The primary key is " + channel + "," + uri + "," + owner);
					System.out.println("The resource name is " + entry2.getValue().getName());
				}
			}
		}
	}
	
	public Resource[] FindReource(IResourceTemplate resourceTemplate){
		Resource[] resources = null;
		
		
		return resources;
	}
}
