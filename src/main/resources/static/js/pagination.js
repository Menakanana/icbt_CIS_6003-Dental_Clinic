/**
 * Universal Data Table Client-Side Pagination Engine
 * Sunrise Dental Clinic Management System
 * 
 * Features:
 * - Default page size: 10
 * - Configurable page sizes: 5, 10, 20, 50, 100
 * - Page navigation (Prev, Next, Page Numbers)
 * - Row counter (e.g. "Showing 1 to 10 of 25 entries")
 * - Compatible with live search filters
 */

function initTablePagination(tableElement, defaultPageSize = 10) {
    if (!tableElement) return;

    // Insert after table or table-responsive wrapper
    const parentContainer = tableElement.classList.contains('data-table') && tableElement.parentElement.classList.contains('table-responsive')
        ? tableElement.parentElement
        : tableElement;

    // Remove old pagination container(s) if present
    let sibling = parentContainer.nextElementSibling;
    while (sibling && sibling.classList.contains('pagination-container')) {
        const toRemove = sibling;
        sibling = sibling.nextElementSibling;
        toRemove.remove();
    }
    let tableSibling = tableElement.nextElementSibling;
    while (tableSibling && tableSibling.classList.contains('pagination-container')) {
        const toRemove = tableSibling;
        tableSibling = tableSibling.nextElementSibling;
        toRemove.remove();
    }

    const paginationContainer = document.createElement('div');
    paginationContainer.className = 'pagination-container';
    
    parentContainer.parentNode.insertBefore(paginationContainer, parentContainer.nextSibling);

    let currentPage = 1;
    let pageSize = defaultPageSize;

    function renderTable() {
        const tbody = tableElement.querySelector('tbody');
        if (!tbody) return;

        const allRows = Array.from(tbody.querySelectorAll('tr'));
        
        // Exclude empty data placeholder row if present
        const dataRows = allRows.filter(row => {
            const firstTd = row.querySelector('td');
            if (firstTd && firstTd.hasAttribute('colspan')) {
                return false;
            }
            return true;
        });

        // Filter rows that are visible under search queries
        const matchingRows = dataRows.filter(row => {
            return row.dataset.searchHidden !== 'true';
        });

        const totalEntries = matchingRows.length;
        const totalPages = Math.ceil(totalEntries / pageSize) || 1;

        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        const startIndex = (currentPage - 1) * pageSize;
        const endIndex = Math.min(startIndex + pageSize, totalEntries);

        // Hide all rows initially
        dataRows.forEach(row => {
            row.style.display = 'none';
        });

        // Show rows for current page index
        matchingRows.slice(startIndex, endIndex).forEach(row => {
            row.style.display = '';
        });

        // Render Pagination UI Controls
        const showingFrom = totalEntries > 0 ? startIndex + 1 : 0;
        const showingTo = endIndex;

        let pageBtnsHtml = '';
        const maxVisibleBtns = 5;
        let startBtn = Math.max(1, currentPage - Math.floor(maxVisibleBtns / 2));
        let endBtn = Math.min(totalPages, startBtn + maxVisibleBtns - 1);
        if (endBtn - startBtn + 1 < maxVisibleBtns) {
            startBtn = Math.max(1, endBtn - maxVisibleBtns + 1);
        }

        for (let i = startBtn; i <= endBtn; i++) {
            pageBtnsHtml += `<button type="button" class="page-btn ${i === currentPage ? 'active' : ''}" data-page="${i}">${i}</button>`;
        }

        paginationContainer.innerHTML = `
            <div class="pagination-info">
                <span>Show</span>
                <select class="page-size-select" title="Rows per page">
                    <option value="5" ${pageSize === 5 ? 'selected' : ''}>5</option>
                    <option value="10" ${pageSize === 10 ? 'selected' : ''}>10</option>
                    <option value="20" ${pageSize === 20 ? 'selected' : ''}>20</option>
                    <option value="50" ${pageSize === 50 ? 'selected' : ''}>50</option>
                    <option value="100" ${pageSize === 100 ? 'selected' : ''}>100</option>
                </select>
                <span>entries</span>
                <span class="entry-count-text" style="margin-left: 0.5rem; color: var(--text-muted);">
                    (Showing <strong>${showingFrom}-${showingTo}</strong> of <strong>${totalEntries}</strong>)
                </span>
            </div>
            <div class="pagination-nav">
                <button type="button" class="page-nav-btn prev-btn" ${currentPage === 1 ? 'disabled' : ''}>&laquo; Prev</button>
                <div class="page-numbers">${pageBtnsHtml}</div>
                <button type="button" class="page-nav-btn next-btn" ${currentPage === totalPages || totalEntries === 0 ? 'disabled' : ''}>Next &raquo;</button>
            </div>
        `;

        // Event listeners for Controls
        const pageSizeSelect = paginationContainer.querySelector('.page-size-select');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                pageSize = parseInt(e.target.value, 10);
                currentPage = 1;
                renderTable();
            });
        }

        const prevBtn = paginationContainer.querySelector('.prev-btn');
        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (currentPage > 1) {
                    currentPage--;
                    renderTable();
                }
            });
        }

        const nextBtn = paginationContainer.querySelector('.next-btn');
        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                if (currentPage < totalPages) {
                    currentPage++;
                    renderTable();
                }
            });
        }

        paginationContainer.querySelectorAll('.page-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                currentPage = parseInt(e.target.getAttribute('data-page'), 10);
                renderTable();
            });
        });
    }

    renderTable();

    // Attach update handler on the table element for live search recalculation
    tableElement.updatePagination = function() {
        currentPage = 1;
        renderTable();
    };
}

// Auto-initialize pagination on load for all .data-table instances
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('table.data-table').forEach(table => {
        initTablePagination(table, 10);
    });
});
