/**
 * Copyright (C) 2014 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * @author: From code in NDN-CPP by Yingdi Yu <yingdi@cs.ucla.edu>
 * See COPYING for copyright and distribution information.
 */

package net.named_data.jndn.security.identity;

import net.named_data.jndn.Data;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyType;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.certificate.IdentityCertificate;
import net.named_data.jndn.util.Blob;
import net.named_data.jndn.util.Common;

/**
 * IdentityStorage is a base class for the storage of identity, public keys and 
 * certificates. Private keys are stored in PrivateKeyStorage.
 * This is an abstract base class.  A subclass must implement the methods.
 */
public abstract class IdentityStorage {
  /**
   * Check if the specified identity already exists.
   * @param identityName The identity name.
   * @return True if the identity exists, otherwise false.
   */
  public abstract boolean 
  doesIdentityExist(Name identityName);

  /**
   * Add a new identity. An exception will be thrown if the identity already 
   * exists.
   * @param identityName The identity name to be added.
   * @throws SecurityException if the identityName is already added.
   */
  public abstract void
  addIdentity(Name identityName) throws SecurityException;
  
  /**
   * Revoke the identity.
   * @return True if the identity was revoked, false if not.
   */
  public abstract boolean
  revokeIdentity();
  
  /**
   * Generate a name for a new key belonging to the identity.
   * @param identityName The identity name.
   * @param useKsk If true, generate a KSK name, otherwise a DSK name.
   * @return The generated key name.
   */
  Name 
  getNewKeyName(Name identityName, boolean useKsk) throws SecurityException
  {
    double ti = Common.getNowMilliseconds();
    // Get the number of seconds.
    String timeString = "" + (long)Math.floor(ti);

    String keyIdStr;
    if (useKsk)
      keyIdStr = ("KSK-" + timeString);
    else
      keyIdStr = ("DSK-" + timeString);

    Name keyName = new Name(identityName).append(keyIdStr);

    if (doesKeyExist(keyName))
      throw new SecurityException("Key name already exists");

    return keyName;
  }

  /**
   * Check if the specified key already exists.
   * @param keyName The name of the key.
   * @return true if the key exists, otherwise false.
   */
  public abstract boolean 
  doesKeyExist(Name keyName);

  /**
   * Add a public key to the identity storage.
   * @param keyName The name of the public key to be added.
   * @param keyType Type of the public key to be added.
   * @param publicKeyDer A blob of the public key DER to be added.
   * @throws SecurityException if a key with the keyName already exists.
   */
  public abstract void 
  addKey(Name keyName, KeyType keyType, Blob publicKeyDer) throws SecurityException;

  /**
   * Get the public key DER blob from the identity storage.
   * @param keyName The name of the requested public key.
   * @return The DER Blob.  If not found, return a Blob with a null pointer.
   */
  public abstract Blob
  getKey(Name keyName);

  /**
   * Activate a key.  If a key is marked as inactive, its private part will not 
   * be used in packet signing.
   * @param keyName The name of the key.
   */
  public abstract void 
  activateKey(Name keyName);

  /**
   * Deactivate a key. If a key is marked as inactive, its private part will not 
   * be used in packet signing.
   * @param keyName The name of the key.
   */
  public abstract void 
  deactivateKey(Name keyName);

  /**
   * Check if the specified certificate already exists.
   * @param certificateName The name of the certificate.
   * @return True if the certificate exists, otherwise false.
   */
  public abstract boolean
  doesCertificateExist(Name certificateName);

  /**
   * Add a certificate to the identity storage.
   * @param certificate The certificate to be added.  This makes a copy of the 
   * certificate.
   * @throws SecurityException if the certificate is already installed.
   */
  public abstract void 
  addCertificate(IdentityCertificate certificate) throws SecurityException;

  /**
   * Get a certificate from the identity storage.
   * @param certificateName The name of the requested certificate.
   * @param allowAny If false, only a valid certificate will be 
   * returned, otherwise validity is disregarded.
   * @return The requested certificate. If not found, return null.
   */
  public abstract Data
  getCertificate(Name certificateName, boolean allowAny);

  /**
   * Get a certificate from the identity storage, requiring only a valid 
   * certificate to be returned.
   * @param certificateName The name of the requested certificate.
   * @return The requested certificate. If not found, return null.
   */
  Data
  getCertificate(Name certificateName)
  {
    return getCertificate(certificateName, false);
  }

  /*****************************************
   *           Get/Set Default             *
   *****************************************/

  /**
   * Get the default identity. 
   * @param return The name of default identity, or an empty name if there is 
   * no default.
   */
  public abstract Name 
  getDefaultIdentity();

  /**
   * Get the default key name for the specified identity.
   * @param identityName The identity name.
   * @return The default key name.
   */
  public abstract Name 
  getDefaultKeyNameForIdentity(Name identityName);

  /**
   * Get the default certificate name for the specified identity.
   * @param identityName The identity name.
   * @return The default certificate name.
   */
  Name 
  getDefaultCertificateNameForIdentity(Name identityName)
  {
    Name keyName = getDefaultKeyNameForIdentity(identityName);    
    return getDefaultCertificateNameForKey(keyName);
  }

  /**
   * Get the default certificate name for the specified key.
   * @param keyName The key name.
   * @return The default certificate name.
   */
  public abstract Name 
  getDefaultCertificateNameForKey(Name keyName);

  /**
   * Set the default identity.  If the identityName does not exist, then clear 
   * the default identity so that getDefaultIdentity() returns an empty name.
   * @param identityName The default identity name.
   */
  public abstract void 
  setDefaultIdentity(Name identityName);

  /**
   * Set the default key name for the specified identity.
   * @param keyName The key name.
   * @param identityNameCheck The identity name to check the keyName.
   */
  public abstract void 
  setDefaultKeyNameForIdentity(Name keyName, Name identityNameCheck);

  /**
   * Set the default key name for the specified identity.
   * @param keyName The key name.
   */
  void 
  setDefaultKeyNameForIdentity(Name keyName)
  {
    setDefaultKeyNameForIdentity(keyName, new Name());
  }

  /**
   * Set the default key name for the specified identity.
   * @param keyName The key name.
   * @param certificateName The certificate name.
   */
  public abstract void 
  setDefaultCertificateNameForKey(Name keyName, Name certificateName);  
}
