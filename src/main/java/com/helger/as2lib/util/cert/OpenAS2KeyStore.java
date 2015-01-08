/**
 * The FreeBSD Copyright
 * Copyright 1994-2008 The FreeBSD Project. All rights reserved.
 * Copyright (C) 2013-2015 Philip Helger philip[at]helger[dot]com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE FREEBSD PROJECT ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE FREEBSD PROJECT OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the FreeBSD Project.
 */
package com.helger.as2lib.util.cert;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;

public class OpenAS2KeyStore implements ICertificateStore
{
  private final KeyStore m_aKeyStore;

  public OpenAS2KeyStore (@Nonnull final KeyStore aKeyStore)
  {
    m_aKeyStore = ValueEnforcer.notNull (aKeyStore, "KeyStore");
  }

  @Nonnull
  public KeyStore getKeyStore ()
  {
    return m_aKeyStore;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <String> getAliases () throws CertificateException
  {
    try
    {
      return ContainerHelper.newList (getKeyStore ().aliases ());
    }
    catch (final KeyStoreException kse)
    {
      throw new CertificateException ("Error getting aliases", kse);
    }
  }

  @Nullable
  public Certificate getCertificate (final String sAlias) throws CertificateException
  {
    try
    {
      return getKeyStore ().getCertificate (sAlias);
    }
    catch (final KeyStoreException kse)
    {
      throw new CertificateException ("Error getting certificate for alias: " + sAlias, kse);
    }
  }

  public void setCertificate (final String sAlias, final Certificate aCert) throws CertificateException
  {
    try
    {
      getKeyStore ().setCertificateEntry (sAlias, aCert);
    }
    catch (final KeyStoreException kse)
    {
      throw new CertificateException ("Error setting certificate: " + sAlias, kse);
    }
  }

  @Nullable
  public String getAlias (@Nonnull final Certificate aCert) throws CertificateException
  {
    try
    {
      return getKeyStore ().getCertificateAlias (aCert);
    }
    catch (final KeyStoreException kse)
    {
      throw new CertificateException ("Error getting alias for certificate: " + aCert.toString (), kse);
    }
  }

  public void removeCertificate (final String sAlias) throws CertificateException
  {
    try
    {
      getKeyStore ().deleteEntry (sAlias);
    }
    catch (final KeyStoreException kse)
    {
      throw new CertificateException ("Error while removing certificate: " + sAlias, kse);
    }
  }

  public void clearCertificates () throws CertificateException
  {
    try
    {
      final KeyStore ks = getKeyStore ();
      final Enumeration <String> aliases = ks.aliases ();
      while (aliases.hasMoreElements ())
      {
        ks.deleteEntry (aliases.nextElement ());
      }
    }
    catch (final KeyStoreException kse)
    {
      throw new CertificateException ("Error clearing certificates", kse);
    }
  }

  @Nullable
  public Key getKey (final String sAlias, final char [] aPassword) throws CertificateException
  {
    try
    {
      return getKeyStore ().getKey (sAlias, aPassword);
    }
    catch (final GeneralSecurityException gse)
    {
      throw new CertificateException ("Error getting key for alias: " + sAlias, gse);
    }
  }

  public void setKey (final String sAlias, final Key aKey, final char [] aPassword) throws CertificateException
  {
    final KeyStore ks = getKeyStore ();
    try
    {
      final Certificate [] certChain = ks.getCertificateChain (sAlias);
      ks.setKeyEntry (sAlias, aKey, aPassword, certChain);
    }
    catch (final KeyStoreException kse)
    {
      throw new CertificateException ("Error setting key for alias: " + sAlias, kse);
    }
  }
}
