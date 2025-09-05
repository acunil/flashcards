import type { ReactNode } from "react";

export interface PageWrapperProps {
  className?: string;
  children: ReactNode;
}

const PageWrapper = ({ className, children }: PageWrapperProps) => {
  return (
    <div className={`${className} min-h-screen flex flex-col w-full`}>
      {children}
    </div>
  );
};
export default PageWrapper;
