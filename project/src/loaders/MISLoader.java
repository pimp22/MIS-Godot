package loaders;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import data_types.MISExternalResource;
import data_types.MISNode;
import data_types.MISScene;
import project.MISProject;

public class MISLoader {

	public static void main(String[] args) {
		MISLoader.loadSceneByLocation("resources/testresources/Sce.tscn");
	}
	
	/**
	 * Reads and returns the loaded scene based on location(file included)
	 * @param location
	 * @return MISScene or null
	 */
	public static MISScene loadSceneByLocation(String location){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(location));
			String readLine = "";
			MISScene scene = new MISScene(1234);
			String[] temp = location.split("/");
			scene.name = temp[temp.length-1].split(".tscn")[0];
			MISNode lastNode = null;
			while((readLine = reader.readLine()) != null){
				if(readLine.startsWith("[ext_resource")){
					MISExternalResource externalResource = new MISExternalResource();
					if(readLine.contains("path=\"")){
						externalResource.path = readLine.split("path=\"")[1].split("\"")[0];
						String[] values = externalResource.path.split("/");
						externalResource.name = values[values.length-1];		
					}
					if(readLine.contains("type=\"")){
						externalResource.type = readLine.split("type=\"")[1].split("\"")[0];
					}
					if(readLine.contains("id=")){
						externalResource.id = Integer.parseInt(readLine.split("id=")[1].split("]")[0]);
					}
					scene.addExternalResource(externalResource);
				} else if(readLine.startsWith("[node")){
					MISNode node = new MISNode();
					if(readLine.contains("name=\"")){
						node.name = readLine.split("name=\"")[1].split("\"")[0];
					}
					if(readLine.contains("type=\"")){
						node.type = readLine.split("type=\"")[1].split("\"")[0];
					}
					if(readLine.contains("parent=\"")){
						String value = readLine.split("parent=\"")[1].split("\"")[0];
						if(!value.equals(".")){
							if(value.contains("/")){
								String[] parents = value.split("/");
								value = parents[parents.length-1];
							}
							for(int i = 0; i < scene.nodeList.size(); i++){
								if(scene.nodeList.get(i).name.equals(value)){
									node.parent = scene.nodeList.get(i);
									break;
								}
							}
						}
					}
					if(readLine.contains("index=\"")){
						node.index = Integer.parseInt(readLine.split("index=\"")[1].split("\"")[0]);
					}
					lastNode = node;
					scene.addNode(node);
				} else if(readLine.startsWith("script = ExtResource")){
					if(lastNode != null){
						readLine = readLine.replaceAll(" ", "");
						String stringOfIndex = readLine.split("\\(")[1].split("\\)")[0];
						lastNode.scriptAttached = true;
						int index = Integer.parseInt(stringOfIndex);
						for(MISExternalResource res : scene.externalResources){
							if(index == res.id){
								lastNode.scriptId = index;
								lastNode.scriptName = res.name;
								break;
							}
						}
					}
				} else if(readLine.startsWith("[gd_scene")){
					String loadStepsString = readLine.split("load_steps=")[1].split(" ")[0];
					scene.loadSteps = Integer.parseInt(loadStepsString);
					String formatString = readLine.split("format=")[1].split("]")[0];
					scene.format = Integer.parseInt(formatString);
				}
			}
			reader.close();
			return scene;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean saveProjectLocation(String[] locations){
		FileWriter file;
		try {
			file = new FileWriter("resources/testresources/projectlocations.txt");
			for(int i = 0; i < locations.length; i++){
				file.write(locations[i]+System.lineSeparator());
			}
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	public static String[] loadProjectLocations(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader("resources/testresources/projectlocations.txt"));
			String readLine = "";
			ArrayList<String> projectLocationsStrings = new ArrayList<String>();
			while((readLine = reader.readLine()) != null){
				projectLocationsStrings.add(readLine);
			}
			String[] returnArray = new String[projectLocationsStrings.size()];
			for(int i = 0; i < returnArray.length; i++){
				returnArray[i] = projectLocationsStrings.get(i);
			}
			reader.close();
			return returnArray;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[]{};
		
	}
	
}
