const hasEditPermission = (permissions, dashboardName) => {
  if (!permissions) return false;
  const dashboard = permissions.find(
    (perm) => perm.dashboard === dashboardName
  );
  return dashboard && dashboard.permissionType === "EDIT";
};
export default hasEditPermission;
