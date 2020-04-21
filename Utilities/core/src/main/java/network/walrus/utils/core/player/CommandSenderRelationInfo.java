package network.walrus.utils.core.player;

import java.util.function.BiPredicate;
import javax.annotation.Nullable;
import network.walrus.common.CommandSender;

/**
 * Information wrapper class containing basic information about how two {@link CommandSender}s
 * relate in terms of identity. This is the basis of the nickname and {@link PlayerTextStyle}
 * systems.
 *
 * <p>It should be noted that this class represents a *one way* relation in the terms of the viewer
 * and target. This means that just because the viewer can see the targets real identity, it should
 * not be assumed that the reverse of this (that the target can see the viewer) is true.
 *
 * <p>It should also be noted that the relation information contained within this class is obtained
 * and cached on instance creation, and is never updated. Because of this, this class should never
 * be stored for long periods of time as the information can become quickly out of date.
 *
 * @author Austin Mayes
 */
public class CommandSenderRelationInfo {

  public static BiPredicate<CommandSender, CommandSender> CAN_SEE_THROUGH_NICK_CHECK =
      (t, v) -> true;

  private final CommandSender target;
  private final CommandSender viewer;
  private final boolean isTrueIdentityPrimary;
  private final boolean canSeeThroughFakeIdentity;
  private final String realName;
  private final @Nullable String fakeName;
  private final String visibleName;

  /**
   * @param target of the relation, that the viewer is seeing
   * @param viewer who is viewing the target
   */
  public CommandSenderRelationInfo(CommandSender target, CommandSender viewer) {
    this.target = target;
    this.viewer = viewer;
    this.isTrueIdentityPrimary = !target.hasFakeDisplayName(viewer);
    this.canSeeThroughFakeIdentity = CAN_SEE_THROUGH_NICK_CHECK.test(target, viewer);
    this.realName = target.getDisplayName(viewer);
    this.fakeName = target.getFakeDisplayName(viewer);
    this.visibleName = isTrueIdentityPrimary ? realName : fakeName;
  }

  /**
   * @return the owner of this identity. This is the person who is being viewed and as the target of
   *     all relations checks used throughout this class.
   */
  public CommandSender target() {
    return target;
  }

  /**
   * @return the viewer of this identity. This is the person who is viewing the target and as the
   *     viewer of all relations checks used throughout this class.
   */
  public CommandSender viewer() {
    return viewer;
  }

  /**
   * @return if the target's real name is the main one visible to the viewer ({@link #realName} and
   *     {@link #visibleName} are the same). This has no relation to the {@link
   *     #canSeeThroughFakeIdentity} property, because a viewer may not be seeing the real identity,
   *     but still may be able to see the targets's real name (if the viewer is a member of staff,
   *     for example).
   */
  public boolean trueIdentityPrimary() {
    return isTrueIdentityPrimary;
  }

  /**
   * @return if the viewer can see through the target's fake identity. While {@link
   *     #isTrueIdentityPrimary} may be false, they still may be able to "peek" through to the real
   *     identity of the target.
   */
  public boolean canSeeThroughFakeIdentity() {
    return canSeeThroughFakeIdentity;
  }

  /**
   * @return the target's real name. which should be the same as {@link #visibleName} if {@link
   *     #isTrueIdentityPrimary} is true.
   */
  public String realName() {
    return realName;
  }

  /**
   * @return the target's fake name as should be seen by the viewer. This will be null if the target
   *     isn't a {@link Player}, or if the target has no fake name set for the viewer.
   */
  @Nullable
  public String fakeName() {
    return fakeName;
  }

  /**
   * @return the current name that the viewer sees for the target. This takes into account all of
   *     the logic mentioned in the above method descriptions.
   */
  public String visibleName() {
    return visibleName;
  }
}
