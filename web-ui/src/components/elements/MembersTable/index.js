import _ from 'lodash';
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

export default ({members}) => {
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

    const rows = _.map(members, (member, id) => _.assign({}, member, { id: id.toString() }));

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
                                        { row.cells.map(cell =><TableCell key={cell.id}>{cell.value}</TableCell>) }
                                    </TableRow>
                                </React.Fragment>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>)}
        />);
}