const DeckListSkeleton = () => {
  const placeholders = Array.from({ length: 5 });

  return (
    <div className="space-y-3 flex flex-col items-center">
      {placeholders.map((_, i) => (
        <div
          key={i}
          className="w-60 h-12 rounded shadow-lg bg-gray-50 animate-pulse border-gray-300 border-2"
        />
      ))}
    </div>
  );
};

export default DeckListSkeleton;
