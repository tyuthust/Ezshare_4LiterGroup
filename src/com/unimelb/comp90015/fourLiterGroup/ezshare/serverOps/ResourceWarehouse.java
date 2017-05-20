package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.sun.media.jfxmedia.logging.Logger;
import com.unimelb.comp90015.fourLiterGroup.ezshare.ServerClass;

public class ResourceWarehouse {

	// Channel,<Uri,<Owner,Resource>>
	private HashMap<String, HashMap<String, HashMap<String, Resource>>> resourceMap;
	
	public ResourceWarehouse() {
		resourceMap = new HashMap<>();
	}
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public boolean AddResource(Resource resource) {
		readWriteLock.writeLock().lock();
		boolean success = true;
		
		try {
			if (resourceMap.containsKey(resource.getChannel())) {
				if (resourceMap.get(resource.getChannel()).containsKey(resource.getURI())) {
					Set<String> owners = resourceMap.get(resource.getChannel()).get(resource.getURI()).keySet();
					// same channel and uri check owner
					for (String string : owners) {
						if (!string.equals(resource.getOwner())) {
							success = false;
							System.out.println("fail to add FIle because diff owner!");
						}
					}
					// true => no different then overwrite
					if (success) {
						resourceMap.get(resource.getChannel())
						.get(resource.getURI())
						.replace(resource.getOwner(),resource);
						System.out.println("SuccessOverwriteFile!");
					}
				} else {
					// uri different => new one
					HashMap<String, Resource> ownerResourceMap = new HashMap<String, Resource>();
					ownerResourceMap.put(resource.getOwner(), resource);

					resourceMap.get(resource.getChannel()).put(resource.getURI(), ownerResourceMap);
					//TODO: add debug mode
					System.out.println("SuccessAddFIle!");
				}
			} else {
				HashMap<String, Resource> ownerResourceMap = new HashMap<String, Resource>();
				ownerResourceMap.put(resource.getOwner(), resource);
				HashMap<String, HashMap<String, Resource>> uriResourceMap = new HashMap<>();
				uriResourceMap.put(resource.getURI(), ownerResourceMap);

				resourceMap.put(resource.getChannel(), uriResourceMap);
				//TODO: add debug mode
				System.out.println("SuccessAddFIle!");
				
			}
		} 
		finally {
			readWriteLock.writeLock().unlock();
		}

		return success;
	}

	public boolean RemoveResource(IResourceTemplate resource) {
		readWriteLock.writeLock().lock();
		boolean success = false;

		try {
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
		} 
		finally {
			readWriteLock.writeLock().unlock();
		}
		return success;
	}

	public boolean RemoveResource(Resource resource) {
		readWriteLock.writeLock().lock();
		boolean success = false;

		try {
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
		} 
		finally {
			readWriteLock.writeLock().unlock();
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
	Resource FindResource(String channel, String uri, String Owner) {
		readWriteLock.readLock().lock();
		Resource resource;
		try {
			resource = null;
			if (resourceMap.containsKey(channel)) {

				if (resourceMap.get(channel).containsKey(uri)) {

					if (resourceMap.get(channel).get(uri).containsKey(Owner)) {
						// same channel uri and owner
						// remove
						resource = resourceMap.get(channel).get(uri).get(Owner);
					}
				}
			} 
		} finally {
			readWriteLock.readLock().unlock();
		}
		return resource;
	}

	public boolean FindResource(String channel, String uri) throws OperationRunningException {
		readWriteLock.readLock().lock();
		
		boolean existResource;
		try {
			existResource = false;
			if (resourceMap.containsKey(channel)) {
				if (resourceMap.get(channel).containsKey(uri)) {
					if (!resourceMap.get(channel).get(uri).isEmpty()) {
						existResource = true;
					}
				}
			} 
		} finally {
			readWriteLock.readLock().unlock();
		}
		return existResource;
	}

	public void printResourceMap(){
		String channel = null;
		String uri = null;
		String owner = null;
		
		readWriteLock.readLock().lock();
		
		try {
			for (HashMap.Entry<String, HashMap<String, HashMap<String, Resource>>> entry : resourceMap.entrySet()) {
				channel = entry.getKey();
				for (HashMap.Entry<String, HashMap<String, Resource>> entry1 : entry.getValue().entrySet()) {
					uri = entry1.getKey();
					for (HashMap.Entry<String, Resource> entry2 : entry1.getValue().entrySet()) {
						owner = entry2.getKey();
						System.out.println("The primary key is: " + channel + "," + uri + "," + owner);
						System.out.println("The resource name is " + entry2.getValue().getName());
					}
				}
			} 
		} finally {
			readWriteLock.readLock().unlock();
		}
	}
	
	public Resource[] FindReource(IResourceTemplate resourceTemplate){
		// R1. (The template channel equals (case sensitive) the resource channel
		// AND
		// R2. If the template contains an owner that is not "", then the candidate owner must equal it (case sensitive) 
		// AND
		// R3. Any tags present in the template also are present in the candidate(case insensitive) 
		// AND
		// R4. If the template contains a URI then the candidate URI matches (case sensitive)
		// AND
			// R5.1 (The candidate name contains the template name as a substring (for non "" template name) 
			// OR
			// R5.2 The candidate description contains the template description as a substring (for non "" template descriptions)
			// OR
			// R5.3 The template description and name are both ""))

		ArrayList<Resource> resources = new ArrayList<>();
		readWriteLock.readLock().lock();
		
		try {
			//R1 channel must match
			HashMap<String, HashMap<String, Resource>> uriMap = resourceMap.get(resourceTemplate.getChannel());
			if (null != uriMap && !uriMap.isEmpty()) {
				//R4 uri match if has in template
				ArrayList<Resource> candidateResourceList;
				if (null != resourceTemplate.getURI() && !resourceTemplate.getURI().isEmpty()) {
					candidateResourceList = new ArrayList<>(uriMap.get(resourceTemplate.getURI()).values());
				} else {
					candidateResourceList = new ArrayList<>();
					for (HashMap<String, Resource> ownerResourceMap : uriMap.values()) {
						candidateResourceList.addAll(ownerResourceMap.values());
					}
				}

				if (null != candidateResourceList && !candidateResourceList.isEmpty()) {
					//R2 owner match if has in template
					if (null != resourceTemplate.getOwner() && !resourceTemplate.getOwner().isEmpty()) {
						//not empty
						//must match
						for (Resource resource : candidateResourceList) {
							if (!resource.getName().equals(resourceTemplate.getName())) {
								candidateResourceList.remove(resource);
							}
						}
					}

					if (null != candidateResourceList && !candidateResourceList.isEmpty()) {
						//R3 resource contains tags
						ArrayList<String> targetTags = new ArrayList<>();
						if (null != resourceTemplate.getTags() && resourceTemplate.getTags().length > 0) {
							for (String string : resourceTemplate.getTags()) {
								targetTags.add(string);
							}
							for (Resource resource : candidateResourceList) {
								//check all targetTags in the resource
								if (checkStringArrayContainsAllListedString(resource.getTags(), targetTags)) {
									candidateResourceList.remove(resource);
								}
							}
						}
					}

					//R5
					for (Resource resource : candidateResourceList) {
						//(!(A or B or C))
						//equals
						//(!A and !B and !C)
						if (!(null != resourceTemplate.getName() && null != resourceTemplate.getDescription()
								&& resourceTemplate.getName().isEmpty() && resourceTemplate.getDescription().isEmpty())
								&& !(null != resource.getDescription()
										&& resource.getDescription().contains(resourceTemplate.getDescription()))
								&& !(null != resource.getName()
										&& resource.getName().contains(resourceTemplate.getName()))) {
							candidateResourceList.remove(resource);
						}
					}
				}
				resources = candidateResourceList;
			} //end of uriMap empty	
			if (0 == resources.size()) {
				return null;
			} else {
				return resources.toArray(new Resource[0]);
			} 
		} finally {
			readWriteLock.readLock().unlock();
		}

	}//end of func
	
	
	private boolean checkStringArrayContainsCertainString(String[] cadidateArray,String checkString){
		if(null!=cadidateArray&&cadidateArray.length>0){
			for (String string : cadidateArray) {
				if(string.equals(checkString))
					return true;			
			}
		}
		return false;
	}
	
	private boolean checkStringArrayContainsAllListedString(String[] cadidateArray,ArrayList<String> checkStringList){
		boolean contains = true;
		if(null!=checkStringList&&checkStringList.size()>0){
			for (String string : checkStringList) {
				contains &= checkStringArrayContainsCertainString(cadidateArray,string);
			}
		}

		return contains;
	}
}
