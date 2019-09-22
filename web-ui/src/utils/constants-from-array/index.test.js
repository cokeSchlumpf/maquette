import constantsFromArray from './index';

it('creates an object of the given constants with a prefix', () => {
    const result = constantsFromArray([
        'BAR',
        'IPSUM'
    ], 'FOO_')

    expect(result.BAR).toEqual('FOO_BAR');
    expect(result.IPSUM).toEqual('FOO_IPSUM');
});

it('creates an object where key equals value if the prefix is undefined', () => {
    const result = constantsFromArray([
        'BAR',
        'IPSUM'
    ]);

    expect(result.BAR).toEqual('BAR');
    expect(result.IPSUM).toEqual('IPSUM');
})