/*
 * SimpleMazeGame.java
 * Copyright (c) 2008, Drexel University.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Drexel University nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY DREXEL UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL DREXEL UNIVERSITY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package maze;

import maze.ui.MazeViewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * 
 * @author Sunny
 * @version 1.0
 * @since 1.0
 */
public class SimpleMazeGame
{
	/**
	 * Creates a small maze.
	 */
	private static Room createRoomWithFourWalls(int num)
	{
		Room room = new Room(num);

		for (Direction dir : Direction.values())
		{
			room.setSide(dir, new Wall());
		}

		return room;
	}

	public static Maze createMaze()
	{

		Maze maze = new Maze();
		Room room1 = createRoomWithFourWalls(0);
		Room room2 = createRoomWithFourWalls(1);

		Door door = new Door(room1, room2);

		room1.setSide(Direction.South, door);
		room2.setSide(Direction.North, door);

		maze.addRoom(room1);
		maze.addRoom(room2);
		return maze;
	}

	public static Maze loadMaze(final String path)
	{
		HashMap<Integer, String[]> roomMap = new HashMap();
		HashMap<String, String[]> doorMap = new HashMap();

		try {
			File file = new File(path);
			Scanner scanner = new Scanner(file);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				String[] splitLine = line.split(" ");

				if (splitLine[0].equals("room"))
				{
					int roomIndex = Integer.parseInt(splitLine[1]);
					String[] roomData = Arrays.copyOfRange(splitLine, 2, splitLine.length);
					roomMap.put(roomIndex, roomData);
				}
				else if (splitLine[0].equals("door"))
				{
					String[] doorData = Arrays.copyOfRange(splitLine, 2, splitLine.length);

					doorMap.put(splitLine[1], doorData);
				}

			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Maze maze = new Maze();

		roomMap.forEach(
				(key, value) ->
				{
					maze.addRoom(new Room(key));
				}
		);

		HashMap<String, Door> instantiatedDoorMap = new HashMap();

		doorMap.forEach(
				(key, value) ->
				{
					int room1Index = Integer.parseInt(value[0]);
					int room2Index = Integer.parseInt(value[1]);

					Door door = new Door(maze.getRoom(room1Index), maze.getRoom(room2Index));

					if (value[2].equals("open"))
					{
						door.setOpen(true);
					}

					instantiatedDoorMap.put(key, door);
				}
		);

		roomMap.forEach(
				(key, value) ->
				{
					String[] stringValues = (String[])value;
					Room room = maze.getRoom((int)key);
					int sideNum = 0;

					for (String mapSite : stringValues)
					{
						Direction currentSide = Direction.values()[sideNum];
						if (mapSite.equals("wall"))
						{
							room.setSide(currentSide, new Wall());
						}
						else if (mapSite.charAt(0) == 'd')
						{
							room.setSide(currentSide, instantiatedDoorMap.get(mapSite));
						}
						else {
							room.setSide(currentSide, maze.getRoom(Integer.parseInt(mapSite)));
						}
						sideNum++;
					}
				}
		);

		return maze;
	}

	public static void main(String[] args)
	{
		Maze maze = loadMaze("large.maze");
	    MazeViewer viewer = new MazeViewer(maze);
	    viewer.run();
	}
}
