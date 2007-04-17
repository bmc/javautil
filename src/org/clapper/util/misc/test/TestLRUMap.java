/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms are permitted provided
  that: (1) source distributions retain this entire copyright notice and
  comment; and (2) modifications made to the software are prominently
  mentioned, and a copy of the original software (or a pointer to its
  location) are included. The name of the author may not be used to endorse
  or promote products derived from this software without specific prior
  written permission.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
  WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.

  Effectively, this means you can do what you want with the software except
  remove this notice or take advantage of the author's name. If you modify
  the software and redistribute your modified version, you must indicate that
  your version is a modification of the original, and you must provide either
  a pointer to or a copy of the original.
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc.test;

import org.clapper.util.misc.LRUMap;
import org.clapper.util.misc.ObjectRemovalEvent;
import org.clapper.util.misc.ObjectRemovalListener;

import org.clapper.util.text.TextUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;

/**
 *
 * @version <tt>$Revision$</tt>
 */
public class TestLRUMap
    extends CommandLineUtility
    implements ObjectRemovalListener
{
    private int initialCapacity = LRUMap.DEFAULT_INITIAL_CAPACITY;
    private int maxCapacity = initialCapacity * 2;
    private float loadFactor = LRUMap.DEFAULT_LOAD_FACTOR;
    private Collection<String[]> keyValuePairs = new ArrayList<String[]>();

    public static void main (String args[])
    {
        TestLRUMap tester = new TestLRUMap();

        try
        {
            tester.execute (args);
        }

        catch (CommandLineUsageException ex)
        {
            // Already reported

            System.exit (1);
        }

        catch (CommandLineException ex)
        {
            System.err.println (ex.getMessage());
            ex.printStackTrace();
            System.exit (1);
        }

        catch (Exception ex)
        {
            ex.printStackTrace (System.err);
            System.exit (1);
        }
    }

    private TestLRUMap()
    {
        super();
    }

    public void objectRemoved (ObjectRemovalEvent event)
    {
        Map.Entry entry = (Map.Entry) event.getSource();

        System.out.println ("*** Removal of " +
                            entry.getKey().toString() +
                            "=" +
                            entry.getValue().toString());
    }

    protected void runCommand()
        throws CommandLineException
    {
        LRUMap<String, String> map;
        Iterator<String[]> it;

        map = new LRUMap<String, String> (initialCapacity,
                                          loadFactor,
                                          maxCapacity);

        map.addRemovalListener (this, false);
        for (it = keyValuePairs.iterator(); it.hasNext(); )
        {
            String[] pair = it.next();

            System.out.println ("Adding: " + pair[0] + "=" + pair[1]);
            map.put (pair[0], pair[1]);
        }

        traverse (map);

        int newCapacity = maxCapacity / 2;
        System.out.println ("Halving size of map to " + newCapacity);
        map.setMaximumCapacity (newCapacity);
        traverse (map);
    }

    protected void parseCustomOption (char             shortOption,
                                      String           longOption,
                                      Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        switch (shortOption)
        {
            case 'i':
                initialCapacity = parseIntOptionArgument (shortOption,
                                                          longOption,
                                                          it.next(),
                                                          1,
                                                          Integer.MAX_VALUE);
                break;

            case 'm':
                maxCapacity = parseIntOptionArgument (shortOption,
                                                      longOption,
                                                      it.next(),
                                                      1,
                                                      Integer.MAX_VALUE);
                break;

            case 'l':
                loadFactor = parseFloatOptionArgument (shortOption,
                                                       longOption,
                                                       it.next(),
                                                       0.1f,
                                                       1.0f);
                break;

            default:
                throw new CommandLineUsageException ("Unrecognized option");
        }
    }
    
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        do
        {
            String s = it.next();
            String[] fields = TextUtil.split (s, "=");
            if (fields.length != 2)
            {
                throw new CommandLineUsageException ("Bad key=value pair: " +
                                                     "\"" +
                                                     s +
                                                     "\"");
            }

            keyValuePairs.add (fields);
        }
        while (it.hasNext());
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addOption ('i', null, "<n>",
                        "Set the initial capacity of the map to <n>.");
        info.addOption ('m', null, "<n>",
                        "Set the maximum capacity of the map to <n>.");
        info.addOption ('l', null, "<f>",
                        "Set the load capacity of the map to <f> (a " +
                        "floating point number)");
        info.addParameter ("key=value ...",
                           "One or more key=value pairs to put in the map.",
                           true);
    }

    private void traverse (Map<String, String> map)
    {
        Iterator<String> it;

        System.out.println ();
        System.out.println ("Traversal by keySet() and get().");
        for (it = map.keySet().iterator(); it.hasNext(); )
        {
            String key = it.next();
            String value = map.get (key);

            if (value != null)
                System.out.println (key.toString() + "=" + value.toString());
            else
                System.out.println (key.toString() + "=null");
        }

        System.out.println ();
        System.out.println ("Traversal by keySet() and get().");
        for (it = map.keySet().iterator(); it.hasNext(); )
        {
            String key = it.next();
            String value = map.get (key);

            if (value != null)
                System.out.println (key.toString() + "=" + value.toString());
            else
                System.out.println (key.toString() + "=null");
        }

        System.out.println ();
        System.out.println ("Traversal by entry set.");
        Iterator<Map.Entry<String, String>> it2;
        for (it2 = map.entrySet().iterator(); it2.hasNext(); )
        {
            Map.Entry<String, String> entry = it2.next();

            System.out.println (entry.getKey() + "=" + entry.getValue());
        }
    }
}
