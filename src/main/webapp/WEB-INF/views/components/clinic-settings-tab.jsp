<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!-- TAB 6: CLINIC PROFILE & SYSTEM SETTINGS COMPONENT -->
<div id="settings-tab" class="tab-content">
    <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 1.5rem;">
        
        <!-- Col 1: Clinic Profile & Facility Charge Form -->
        <div class="card-panel">
            <h3 style="margin-bottom: 1rem; color: var(--primary);">🏥 Clinic Profile & Operational Charges</h3>
            <p style="color: var(--text-muted); font-size: 0.85rem; margin-bottom: 1.25rem;">Configure clinic business information and static facility fees.</p>

            <form action="${pageContext.request.contextPath}/settings/clinic/save" method="post">
                <div class="form-group">
                    <label>Official Clinic Name</label>
                    <input type="text" name="clinicName" class="form-control" value="<c:out value='${clinicName}' default='Sunrise Dental Clinic'/>" required>
                </div>

                <div class="form-group">
                    <label>Clinic Address</label>
                    <input type="text" name="clinicAddress" class="form-control" value="<c:out value='${clinicAddress}' default='123 Galle Road, Colombo 03'/>" required>
                </div>

                <div class="form-group">
                    <label>Contact Phone Numbers</label>
                    <input type="text" name="clinicPhone" class="form-control" value="<c:out value='${clinicPhone}' default='011-2345678 / 077-1234567'/>" required>
                </div>

                <div class="form-group" style="background-color: #ECFDF5; border: 1px solid #A7F3D0; padding: 1rem; border-radius: var(--radius-sm);">
                    <label style="color: #065F46; font-weight: 700;">Static Clinic Facility Charge (LKR)</label>
                    <input type="number" step="0.01" min="0" name="clinicCharge" class="form-control" value="<c:out value='${clinicCharge}' default='500.00'/>" required style="font-weight: 700; color: #047857; font-size: 1.05rem;">
                    <small style="color: #047857; display: block; margin-top: 0.25rem;">This charge is automatically added to all patient consultation bills.</small>
                </div>

                <button type="submit" class="btn-primary" style="width: 100%; padding: 0.75rem;">💾 Save Clinic Profile & Charges</button>
            </form>
        </div>

        <!-- Col 2: Treatment Procedure Tariff Catalog Management -->
        <div class="card-panel">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                <h3 style="color: var(--primary); margin: 0;">🦷 Treatment Procedure Tariff Catalog</h3>
                <button type="button" class="btn-primary" onclick="openAddTreatmentModal()" style="font-size: 0.85rem; padding: 0.4rem 0.8rem;">+ Add New Procedure</button>
            </div>

            <!-- Treatment Form Container (Inline Card) -->
            <div id="treatmentFormCard" style="display: none; background: #F8FAFC; border: 1px dashed var(--primary); padding: 1.25rem; border-radius: var(--radius-sm); margin-bottom: 1.5rem;">
                <h4 id="treatmentFormTitle" style="color: var(--primary); margin-bottom: 0.75rem;">Add New Treatment Procedure</h4>
                <form action="${pageContext.request.contextPath}/settings/treatment/save" method="post" id="treatmentForm">
                    <input type="hidden" name="treatmentTypeId" id="treatmentIdInput">
                    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1rem;">
                        <div class="form-group">
                            <label>Procedure Name</label>
                            <input type="text" name="treatmentName" id="treatmentNameInput" class="form-control" required placeholder="e.g. Root Canal Treatment">
                        </div>
                        <div class="form-group">
                            <label>Base Cost (LKR)</label>
                            <input type="number" step="0.01" min="0" name="baseCost" id="treatmentCostInput" class="form-control" required placeholder="e.g. 5000.00">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Description / Category</label>
                        <input type="text" name="description" id="treatmentDescInput" class="form-control" placeholder="e.g. Endodontic procedure for infected tooth root">
                    </div>
                    <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                        <button type="submit" class="btn-primary">💾 Save Procedure</button>
                        <button type="button" class="btn-logout" onclick="closeTreatmentForm()">Cancel</button>
                    </div>
                </form>
            </div>

            <!-- Tariff Table -->
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Procedure ID</th>
                            <th>Treatment Name</th>
                            <th>Description</th>
                            <th>Base Cost (LKR)</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${treatmentTypes}" var="t">
                            <tr>
                                <td><strong>T-<c:out value="${t.treatmentTypeId}"/></strong></td>
                                <td><strong><c:out value="${t.treatmentName}"/></strong></td>
                                <td><c:out value="${t.description}"/></td>
                                <td><strong style="color: var(--primary);">LKR <fmt:formatNumber value="${t.baseCost}" type="currency" currencySymbol=""/></strong></td>
                                <td>
                                    <button type="button" class="btn-primary" style="padding: 0.25rem 0.6rem; font-size: 0.75rem;" 
                                            onclick="editTreatment('${t.treatmentTypeId}', '${t.treatmentName}', '${t.description}', '${t.baseCost}')">✏️ Edit Price</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty treatmentTypes}">
                            <tr>
                                <td colspan="5" style="text-align: center; color: var(--text-muted);">No treatment procedures configured yet.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

    </div>
</div>

<script>
    function openAddTreatmentModal() {
        document.getElementById('treatmentFormTitle').innerText = 'Add New Treatment Procedure';
        document.getElementById('treatmentIdInput').value = '';
        document.getElementById('treatmentNameInput').value = '';
        document.getElementById('treatmentCostInput').value = '';
        document.getElementById('treatmentDescInput').value = '';
        document.getElementById('treatmentFormCard').style.display = 'block';
    }

    function editTreatment(id, name, desc, cost) {
        document.getElementById('treatmentFormTitle').innerText = 'Edit Treatment Procedure & Price';
        document.getElementById('treatmentIdInput').value = id;
        document.getElementById('treatmentNameInput').value = name;
        document.getElementById('treatmentDescInput').value = desc;
        document.getElementById('treatmentCostInput').value = cost;
        document.getElementById('treatmentFormCard').style.display = 'block';
        window.scrollTo({ top: document.getElementById('treatmentFormCard').offsetTop - 50, behavior: 'smooth' });
    }

    function closeTreatmentForm() {
        document.getElementById('treatmentFormCard').style.display = 'none';
    }
</script>
