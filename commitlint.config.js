module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [
      2,
      'always',
      [
        'build',
        'chore',
        'ci',
        'docs',
        'feat',
        'fix',
        'perf',
        'refactor',
        'revert',
        'style',
        'test',
      ],
    ],
    'scope-enum': [
      2,
      'always',
      [
        // Cross-cutting technical concerns
        'network',
        'database',
        'content',
        'notification',
        'di',
        'navigation',
        'theme',
        'common',
        'gradle',
        'deps',
        // Yadlo feature slices
        'home',
        'programme',
        'mon-yadlo',
        'happening',
        'plus',
        // Template example feature — remove together with feature/pokemonexplorer/
        'pokemon-explorer',
      ],
    ],
    'scope-empty': [0],
    'scope-required-on-types': [2, 'always'],
  },
  plugins: [
    {
      rules: {
        'scope-required-on-types': ({type, scope}) => {
          const requiredTypes = ['feat', 'fix', 'refactor', 'build'];
          const isRequired = requiredTypes.includes(type);
          if (isRequired && !scope) {
            return [false, `scope is required for types: ${requiredTypes.join(', ')}` ];
          }
          return [true];
        },
      },
    },
  ],
};
