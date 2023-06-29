package com.hhs.zzzyt.modloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.hhs.zzzyt.modloader.annotation.Mod;
import com.hhs.zzzyt.modloader.exception.UnformattedModIDException;
import com.hhs.zzzyt.modloader.exception.UnformattedModVersionException;

/**
 * 
 * Main class of the mod loading system.All mods are loaded and initialised
 * here.<br>
 * <br>
 * It is also used as an API.
 * 
 * @version 1.1.0
 * @author Zzzyt
 * 
 */
public class MainLoader {

	static Map<String, HashMap<String, Class<?>>> LoadedClasses = new HashMap<String, HashMap<String, Class<?>>>();
	static List<String> ModIDs = new ArrayList<String>();
	static Map<String, String> ModNames = new HashMap<String, String>();
	static Map<String, String> ModVersions = new HashMap<String, String>();

	public static void main(String[] args) {
		load(new File("mods"));
	}
	
	/**
	 * Load the mods!
	 * 
	 * @param ModJarDir The directory where the mods are stored
	 */
	public static void load(File ModJarDir) {
		// TEST AREA
		// System.out.println(("10.00.00").split("\\p{Punct}.")[0]);
		// System.out.println(getFormatName("mod1/ClassA.class"));

		if (!ModJarDir.exists())
			ModJarDir.mkdirs();
		File[] ModPaths = ModJarDir.listFiles();

		List<ClassLoader> Loaders = new ArrayList<ClassLoader>();
		List<Class<?>> InitClasses = new ArrayList<Class<?>>();

		// Get class loaders
		URL JarFileURL;
		try {
			for (int i = 0; i < ModPaths.length; i++) {
				// get URL of the jar file
				JarFileURL = ModPaths[i].toURI().toURL();
				// System.out.println(JarFileURL);

				// get URL class loader
				ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
				Loaders.add(new URLClassLoader(new URL[] { JarFileURL }, systemClassLoader));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// load the Mods
		try {
			for (int i = 0; i < ModPaths.length; i++) {
				// Get the jar file
				JarFile CurrentJar = new JarFile(ModPaths[i], false);
				String JarName = getJarName(CurrentJar.getName());

				// Get all resources in it
				Enumeration<JarEntry> CurrentEntries = CurrentJar.entries();

				System.out.println("Reading Jar File:" + CurrentJar.getName());

				String CurrentModID = "Default Mod Name";
				HashMap<String, Class<?>> ClassBuffer = new HashMap<String, Class<?>>();

				// Read the resources
				while (CurrentJar.entries().hasMoreElements()) {
					String FullName;
					try {
						FullName = CurrentEntries.nextElement().getName();
					} catch (NoSuchElementException ee) {
						break;
					}

					System.out.println("    Reading File:" + FullName);
					String FormatedName = getFormatName(FullName);

					// If it's a class file
					if (FullName.endsWith(".class")) {
						// Put it in a buffer
						ClassBuffer.put(FormatedName, Loaders.get(i).loadClass(FormatedName));

						if (ClassBuffer.get(FormatedName).getAnnotations().length == 1) {
							if (ClassBuffer.get(FormatedName).getAnnotations()[0].annotationType().equals(Mod.class)) {
								// This is the init class
								System.out.println("        In Jar file #" + i);
								System.out.println("        In Class file " + FormatedName);

								// Get mod data from annotation
								Mod CurrentAnnotaton = ClassBuffer.get(FormatedName).getAnnotation(Mod.class);
								CurrentModID = CurrentAnnotaton.modid();
								String CurrentModName = CurrentAnnotaton.name();
								String CurrentModVersion = CurrentAnnotaton.version();

								checkModVersionFormat(CurrentModVersion);
								checkModIDFormat(CurrentModID);

								ModIDs.add(CurrentModID);
								ModNames.put(JarName, CurrentModName);
								ModVersions.put(JarName, CurrentModVersion);

								System.out.println("        Mod Info:");
								System.out.println("            Mod ID:" + CurrentModID);
								System.out.println("            Mod Name:" + CurrentModName);
								System.out.println("            Mod Version:" + CurrentModVersion);

								// Add it to init class list
								InitClasses.add(ClassBuffer.get(FormatedName));
							}
						}
					}
				}
				// Put buffer into class library
				LoadedClasses.put(CurrentModID, ClassBuffer);

				CurrentJar.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Init
		System.out.println("\n----------------Init Mods----------------");
		for (int i = 0; i < InitClasses.size(); i++) {
			ModInit CurrentMod = null;
			try {
				// Create instance of mod init class
				CurrentMod = (ModInit) InitClasses.get(i).newInstance();

				CurrentMod.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("\n\n");
		}
		System.out.println("----------------Init  End----------------");
	}

	/**
	 * 
	 * Get a class file from given mod ID and class name. <br>
	 * <br>
	 * In this format: <br>
	 * <code>getModClass("TestMod","com.hhs.xiaomao.ModClass1")</code>
	 * 
	 * @param ModID     ID of the mod.
	 * @param ClassName Name of the queried class.
	 * @return
	 */
	public static Class<?> getModClass(String ModID, String ClassName) {
		return LoadedClasses.get(ModID).get("ModName");
	}

	/**
	 * Check whether a mod is loaded.
	 * 
	 * @param ModID Mod ID of the querying mod.
	 * @return Whether the mod is loaded.
	 */
	public static boolean isModLoaded(String ModID) {
		return ModIDs.contains(ModID);
	}

	/**
	 * Get all loaded mods.
	 * 
	 * @return A list of all loaded mods.
	 */
	public static List<String> getMods() {
		return ModIDs;
	}

	/**
	 * Get name of mod by mod ID.<br>
	 * <br>
	 * <i>Actually use mod ID is better than mod name,because mod name may not be
	 * formatted.</i>
	 * 
	 * @param ModID Mod ID of the the querying mod.
	 * @return Name of the mod.
	 */
	public static String getModName(String ModID) {
		return ModNames.get(ModID);
	}

	/**
	 * Get version of mod by mod ID.
	 * 
	 * @param ModID Mod ID of the the querying mod.
	 * @return Version of the mod.
	 */
	public static String getModVersion(String ModID) {
		return ModVersions.get(ModID);
	}

	/**
	 * 
	 * Compare two mod versions.<br>
	 * When version1 is newer(or later) than Version2,it returns <code>1</code>.<br>
	 * When version2 is newer(or later) than Version1,it returns
	 * <code>-1</code>.<br>
	 * When version1 is the same as Version2,it returns <code>0</code>.<br>
	 * <br>
	 * <i>Little Tip:</i><br>
	 * You can use it as:<br>
	 * <br>
	 * <code>versionCompare("1.8.0","1.6.4") > 0</code><br>
	 * <br>
	 * Where you can change the symbol '>' to '<','<=','>=','==','!=' to do the
	 * comperation.
	 * 
	 * @param Version1 The first verion to compare.
	 * @param Version2 The second verion to compare.
	 * @return Result of the comperation.
	 * 
	 * @throws UnformattedModVersionException while given unformatted mod version.
	 * 
	 * @see String#compareTo(String)
	 */
	public static int versionCompare(String Version1, String Version2) {
		String SubVersion1[] = Version1.split(".");
		String SubVersion2[] = Version2.split(".");

		if (SubVersion1.length != 3 || SubVersion2.length != 3) {
			throw (new UnformattedModVersionException("Comparing " + Version1 + " to " + Version2));
		}
		int a1, b1, c1, a2, b2, c2;
		try {
			a1 = Integer.parseInt(SubVersion1[0]);
			b1 = Integer.parseInt(SubVersion1[1]);
			c1 = Integer.parseInt(SubVersion1[2]);
			a2 = Integer.parseInt(SubVersion2[0]);
			b2 = Integer.parseInt(SubVersion2[1]);
			c2 = Integer.parseInt(SubVersion2[2]);
		} catch (Exception e) {
			throw (new UnformattedModVersionException("Comparing " + Version1 + " to " + Version2));
		}
		if (a1 < 0 || b1 < 0 || c1 < 0 || a2 < 0 || b2 < 0 || c2 < 0) {
			throw (new UnformattedModVersionException("Comparing " + Version1 + " to " + Version2));
		}

		if (a1 > a2) {
			return 1;
		} else if (a1 < a2) {
			return -1;
		} else {
			if (b1 > b2) {
				return 1;
			} else if (b1 < b2) {
				return -1;
			} else {
				if (c1 > c2) {
					return 1;
				} else if (c1 < c2) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	/**
	 * 
	 * Check whether your version is later than <strong>or equal to</strong> the
	 * version to compare.<br>
	 * <br>
	 * <i>Little Tip : It's a convinient way to check compatibility.</i><br>
	 * <br>
	 * Same as:<br>
	 * <code>versionCompare(Version1, VersionToCompare) >= 0</code>
	 * 
	 * @param Version1         Your version.
	 * @param VersionToCompare Version to compare.
	 * @return Whether your version is later than <strong>or equal to</strong> the
	 *         version to compare.
	 * 
	 * @see MainLoader#versionCompare(String, String)
	 */
	public static boolean isLaterVersion(String Version1, String VersionToCompare) {
		return versionCompare(Version1, VersionToCompare) >= 0;
	}

	// API functions
	//
	// ~~~~~~~~~~~~~~~~~~~~~~~~Beautiful Line~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//
	// Inner name helpers

	/**
	 * 
	 * Remove the extension name and replace '/' with '.' in a URL for a resource in
	 * a jar file. <br>
	 * <br>
	 * <code>getFormatName("mod1/ClassA.class") = "mod1.ClassA"</code>
	 * 
	 * @param FullName URL of a resource in a jar file.
	 * @return Fromated name of this resource.
	 */
	private static String getFormatName(String FullName) {
		return getNoExtensionName(FullName).replace('/', '.');
	}

	/**
	 * Get the anem of a jar file (without extension name).
	 * 
	 * @param FullName Full path to the jar file.
	 * @return Name of the jar file.
	 */
	private static String getJarName(String FullName) {
		FullName = getNoExtensionName(FullName);
		return FullName.split("/")[FullName.split("/").length - 1];
	}

	/**
	 * Clear the extension name from a file name.
	 * 
	 * @param FileName Full file name.
	 * @return File name after clearing.
	 */
	private static String getNoExtensionName(String FileName) {
		for (int i = FileName.length() - 1; i >= 0; i--) {
			if (FileName.charAt(i) == '.') {
				return FileName.substring(0, i);
			}
		}
		return FileName;
	}

	/**
	 * Check if a mod version is formatted.<br>
	 * Throws an exception if not formatted.
	 * 
	 * @param ModVersion Mod version to check.
	 * 
	 * @throws UnformattedModVersionException while the mod version is not
	 *                                        formatted.
	 */
	private static void checkModVersionFormat(String ModVersion) {
		String SubVersion[] = ModVersion.split("\\.");

		if (SubVersion.length != 3) {
			throw (new UnformattedModVersionException(ModVersion));
		}
		int a1, b1, c1;
		try {
			a1 = Integer.parseInt(SubVersion[0]);
			b1 = Integer.parseInt(SubVersion[1]);
			c1 = Integer.parseInt(SubVersion[2]);
		} catch (Exception e) {
			throw (new UnformattedModVersionException(ModVersion));
		}
		if (a1 < 0 || b1 < 0 || c1 < 0) {
			throw (new UnformattedModVersionException(ModVersion));
		}

	}

	/**
	 * Check if a mod ID is formatted.<br>
	 * Throws an exception if not formatted.
	 * 
	 * @param ModID Mod ID to check.
	 * 
	 * @throws UnformattedModIDException while te mod ID is not formatted.
	 */
	private static void checkModIDFormat(String ModID) {
		for (int i = 0; i < ModID.length(); i++) {
			if (!(('0' <= ModID.charAt(i) && ModID.charAt(i) <= '9')
					|| ('a' <= ModID.charAt(i) && ModID.charAt(i) <= 'z')
					|| ('A' <= ModID.charAt(i) && ModID.charAt(i) <= 'Z'))) {
				throw (new UnformattedModIDException(ModID));
			}
		}
	}

}
