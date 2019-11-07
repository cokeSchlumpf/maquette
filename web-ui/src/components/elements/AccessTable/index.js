import React from 'react';
import './styles.scss';

import {
    DataTable,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableHeader,
    TableRow
} from 'carbon-components-react';

export default ({children}) => {
    const headers = [
        {
            key: 'granted-to',
            header: 'Granted To',
        },
        {
            key: 'privilege',
            header: 'Privilege',
        },
        {
            key: 'granted-by',
            header: 'Granted By',
        },
        {
            key: 'granted-at',
            header: 'Granted At',
        }
    ];

    const rows = [
        {
            "id": "1",
            "granted-to": "hippo",
            "privilege": "god",
            "granted-by": "hippo",
            "granted-at": "23min ago"
        },
        {
            "id": "2",
            "granted-to": "sackgesicht",
            "privilege": "little-god",
            "granted-by": "hippo",
            "granted-at": "23min ago"
        }
    ];

    return (
        <DataTable
            isSortable={ true }
            rows={ rows }
            headers={headers}
            render={({ rows, headers, getHeaderProps, getRowProps, getTableProps }) => (
                <TableContainer>
                    <Table {...getTableProps()}>
                        <TableHead>
                            <TableRow>
                                {
                                    headers.map(header => <TableHeader {...getHeaderProps({ header })}>{header.header}</TableHeader>)
                                }
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows.map(row => (
                                <React.Fragment key={ "key-" + row.id }>
                                    <TableRow {...getRowProps({ row })}>
                                        {row.cells.map(cell => cell.info.header !== "description" &&
                                                               <TableCell key={cell.id}>{cell.value}</TableCell>
                                        )}
                                    </TableRow>
                                </React.Fragment>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>)}
        />);
}