//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2003 Sep 29: Modifications to allow for OpenNMS to handle duplicate IP Addresses.
// 2003 Jan 31: Cleaned up some unused imports.
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.                                                            
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//       
// For more information contact: 
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
// Tab Size = 8
//

package org.opennms.netmgt.capsd.snmp;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Category;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.utils.Signaler;
import org.opennms.protocols.snmp.SnmpSession;
import org.opennms.protocols.snmp.SnmpVarBind;

/**
 * <P>
 * IpAddrTable uses a SnmpSession to collect the ipAddrTable entries It
 * implements the SnmpHandler to receive notifications when a reply is
 * received/error occurs in the SnmpSession used to send requests /recieve
 * replies.
 * </P>
 * 
 * @author <A HREF="mailto:jamesz@opennms.org">James Zuo </A>
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya </A>
 * @author <A HREF="mailto:weave@oculan.com">Weave </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * 
 * @see <A HREF="http://www.ietf.org/rfc/rfc1213.txt">RFC1213 </A>
 */
public class IpAddrTable extends SnmpTableWalker {

    /**
     * <P>
     * The list of collected IpAddrTableEntries built from the infomation
     * collected from the remote agent.
     * </P>
     */
    private List m_entries;

    /**
     * <P>
     * Constructs an IpAddrTable object that is used to collect the address
     * elements from the remote agent. Once all the elements are collected, or
     * there is an error in the collection the signaler object is <EM>notified
     * </EM> to inform other threads.
     * </P>
     * @param session
     *            The session with the remote agent.
     * @param address TODO
     * @param signaler
     *            The object to notify waiters.
     * 
     * @see IpAddrTableEntry
     */
    public IpAddrTable(SnmpSession session, InetAddress address, Signaler signaler, int version) {
        super(address, signaler, version, "ipAddrTable", IpAddrTableEntry.getElements(), IpAddrTableEntry.TABLE_OID);
        m_entries = new ArrayList();
        start(session);
    }

    /**
     * <P>
     * Returns the list of entry maps that can be used to access all the
     * information about the interface table.
     * </P>
     * 
     * @return The list of ifTableEntry maps.
     */
    public List getEntries() {
        return m_entries;
    }

    protected void processTableRow(SnmpVarBind[] vblist) {
        IpAddrTableEntry ent = new IpAddrTableEntry(vblist);
        m_entries.add(ent);
    }

    /**
     * <P>
     * This method is used to find the corresponding IP Address for the indexed
     * interface. The list of IP Address entries are searched until <EM>the
     * first</EM> IP Address is found for the interface. The IP Address is then
     * returned as a string. If there is no interface corresponding to the index
     * then a null is returned to the caller.
     * </P>
     * 
     * @param ipAddrEntries
     *            List of IpAddrTableEntry objects to search
     * @param ifIndex
     *            The interface index to search for
     * 
     * @return IP Address for the indexed interface.
     */
    public static InetAddress getIpAddress(List ipAddrEntries, int ifIndex) {
        if (ifIndex == -1 || ipAddrEntries == null) {
            return null;
        }

        Iterator iter = ipAddrEntries.iterator();
        while (iter.hasNext()) {
            IpAddrTableEntry ipAddrEntry = (IpAddrTableEntry) iter.next();

            Integer snmpIpAddrIndex = ipAddrEntry.getIpAdEntIfIndex();

            if (snmpIpAddrIndex == null) {
                continue;
            }

            int ipAddrIndex = snmpIpAddrIndex.intValue();

            if (ipAddrIndex == ifIndex) {
                return ipAddrEntry.getIpAdEntAddr();
            }

        }

        return null;
    }

    /**
     * Returns all Internet addresses at the corresponding index. If the address
     * cannot be resolved then a null reference is returned.
     * 
     * @param ipAddrEntries
     *            List of IpAddrTableEntry objects to search
     * @param ifIndex
     *            The index to search for.
     * 
     * @return list of InetAddress objects representing each of the interfaces
     *         IP addresses.
     */
    public static List getIpAddresses(List ipAddrEntries, int ifIndex) {
        if (ifIndex == -1 || ipAddrEntries == null) {
            return null;
        }

        List addresses = new ArrayList();

        Iterator i = ipAddrEntries.iterator();
        while (i.hasNext()) {
            IpAddrTableEntry entry = (IpAddrTableEntry) i.next();
            Integer ndx = entry.getIpAdEntIfIndex();
            if (ndx != null && ndx.intValue() == ifIndex) {
                
                InetAddress ifAddr = entry.getIpAdEntAddr();
                if (ifAddr != null) {
                    addresses.add(ifAddr);
                }
            }
        }
        return addresses;
    }

    /**
     * Returns all Internet addresses in the ipAddrEntry list. If the address
     * cannot be resolved then a null reference is returned.
     * 
     * @param ipAddrEntries
     *            List of IpAddrTableEntry objects to search
     * 
     * @return list of InetAddress objects representing each of the interfaces
     *         IP addresses.
     */
    public static List getIpAddresses(List ipAddrEntries) {
        if (ipAddrEntries == null) {
            return null;
        }

        List addresses = new ArrayList();

        Iterator i = ipAddrEntries.iterator();
        while (i.hasNext()) {
            IpAddrTableEntry entry = (IpAddrTableEntry) i.next();
            Integer ndx = entry.getIpAdEntIfIndex();
            if (ndx != null) {

                InetAddress ifAddr = entry.getIpAdEntAddr();
                if (ifAddr != null) {
                    addresses.add(ifAddr);
                }

            }
        }
        return addresses;
    }

    /**
     * <P>
     * This method is used to find the ifIndex of an interface given the
     * interface's IP address. The list of ipAddrTable entries are searched
     * until an interface is found which has a matching IP address. The ifIndex
     * of that interface is then returned. If no match is found -1 is returned.
     * 
     * @param ipAddrEntries
     *            List of IpAddrTableEntry objects to search
     * @param ipAddress
     *            The IP address to search for
     * 
     * @return ifIndex of the interface with the specified IP address
     */
    public static int getIfIndex(List ipAddrEntries, String ipAddress) {
        if (ipAddress == null) {
            return -1;
        }

        Iterator iter = ipAddrEntries.iterator();
        while (iter.hasNext()) {
            IpAddrTableEntry ipAddrEntry = (IpAddrTableEntry) iter.next();
            InetAddress snmpAddr = ipAddrEntry.getIpAdEntAddr();
            if (ipAddress.equals(snmpAddr.toString())) {
                Integer snmpIpAddrIndex = ipAddrEntry.getIpAdEntIfIndex();
                return snmpIpAddrIndex.intValue();
            } else
                continue;
        }

        return -1;
    }

    /**
     * <P>
     * This method is used to find the corresponding netmask for the indexed
     * interface. The list of IP Address table entries are searched until <EM>
     * the first</EM> netmask address is found for the interface. The netmask
     * is then returned as a string. If there is no interface corresponding to
     * the index then a null is returned.
     * </P>
     * 
     * @param ipAddrEntries
     *            List of IpAddrTableEntry objects to search
     * @param ifIndex
     *            The interface index to search for.
     * 
     * @return The netmask for the interface.
     */
    public static String getNetmask(List ipAddrEntries, int ifIndex) {
        if (ifIndex == -1) {
            return null;
        }

        Iterator iter = ipAddrEntries.iterator();
        while (iter.hasNext()) {
            IpAddrTableEntry ipAddrEntry = (IpAddrTableEntry) iter.next();
            Integer snmpIpAddrIndex = ipAddrEntry.getIpAdEntIfIndex();
            if (snmpIpAddrIndex == null) {
                continue;
            }

            int ipAddrIndex = snmpIpAddrIndex.intValue();
            if (ipAddrIndex == ifIndex) {
                InetAddress snmpAddr = ipAddrEntry.getIpAdEntNetMask();
                return (snmpAddr == null ? null : snmpAddr.getHostAddress());
            }
        }

        return null;
    }

    public InetAddress[] getIfAddressAndMask(int ifIndex) {
        if (getEntries() == null)
            return null;
        
        Iterator i = getEntries().iterator();
        while (i.hasNext()) {
            IpAddrTableEntry entry = (IpAddrTableEntry) i.next();
            Integer ndx = entry.getIpAdEntIfIndex();
            if (ndx != null && ndx.intValue() == ifIndex) {
                // found it
                // extract the address
                //
                InetAddress[] pair = new InetAddress[2];
                pair[0] = entry.getIpAdEntAddr();
                pair[1] = entry.getIpAdEntNetMask();
                return pair;
            }
        }
        return null;
    }

    public int getIfIndex(InetAddress address) {
        if (getEntries() == null) {
            return -1;
        }
        if (log().isDebugEnabled())
            log().debug("getIfIndex: num ipAddrTable entries: " + getEntries().size());
        Iterator i = getEntries().iterator();
        while (i.hasNext()) {
            IpAddrTableEntry entry = (IpAddrTableEntry) i.next();
            InetAddress ifAddr = entry.getIpAdEntAddr();
            if (ifAddr != null && ifAddr.equals(address)) {
                // found it
                // extract the ifIndex
                //
                Integer ndx = entry.getIpAdEntIfIndex();
                log().debug("getIfIndex: got a match for address " + address.getHostAddress() + " index: " + ndx);
                if (ndx != null)
                    return ndx.intValue();
            }
        }
        log().debug("getIfIndex: no matching ipAddrTable entry for " + address.getHostAddress());
        return -1;
    }

    private final Category log() {
        return ThreadCategory.getInstance(IpAddrTable.class);
    }
}
