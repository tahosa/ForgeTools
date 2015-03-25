package net.randallcrock.forgetools.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import net.minecraft.world.chunk.Chunk;


/**
 * Wrapper for chunks to add a count of how many loose items there are
 */
public class ItemChunkRef implements Comparable
{

	/**
	 * Comparator for using built in sort() methods on ChunkRef objects
	 */
	protected static class ItemChunkRefComparator implements Comparator<Object>
	{
		@Override
		public int compare(Object o1, Object o2) {
			if(!(o1 instanceof Comparable))
				return 0;
			return ((Comparable)o1).compareTo(o2);
		}
		
	}
	
	private Chunk _chunk;
	private int _val;
	
	public ItemChunkRef(Chunk c, int value)
	{
		_chunk = c;
		_val = value;
	}
	
	public Chunk getChunk()
	{
		return _chunk;
	}
	
	public int getValue()
	{
		return _val;
	}
	
	@Override
	public int compareTo(Object o)
	{
		if(o instanceof ItemChunkRef)
		{
			ItemChunkRef c = (ItemChunkRef)o;
			if (c.getValue() < this.getValue())
				return -1;
			else if(c.getValue() == this.getValue())
				return 0;
			else return 1;
		}
		
		return 0;
	}
	
	/**
	 * Get the list of chunks sorted by descending item count
	 * @param chunks Map of chunks and the number of tiems they contain
	 * @return Sorted list
	 */
	public static ItemChunkRef[] getSortedChunkList(HashMap<Chunk, Integer> chunks)
	{
		ArrayList<ItemChunkRef> list = new ArrayList<ItemChunkRef>(); 
		for(Chunk c : chunks.keySet())
		{
			list.add(new ItemChunkRef(c, chunks.get(c)));
		}
		
		ItemChunkRef[] ret = list.toArray(new ItemChunkRef[] {});
		Arrays.sort(ret, new ItemChunkRefComparator());
		return ret;
	}
}
